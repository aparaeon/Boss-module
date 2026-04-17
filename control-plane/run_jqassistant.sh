rm -f run/cobblemon/jqa-backend-targets/* 2>/dev/null || true
rm -f run/cobblemon/jqa-proxy-targets/* 2>/dev/null || true

mkdir -p run/cobblemon/jqa-backend-targets
mkdir -p run/cobblemon/jqa-proxy-targets

find run/cobblemon/spawn/mods/ \( -name "*module*.jar" -o -name "*loader*.jar" \) -exec cp {} run/cobblemon/jqa-backend-targets/ \;
find run/cobblemon/proxy/plugins/ \( -name "*module*.jar" -o -name "*loader*.jar" \) -exec cp {} run/cobblemon/jqa-proxy-targets/ \;

for dir in run/cobblemon/jqa-backend-targets run/cobblemon/jqa-proxy-targets; do
  for jar in "$dir"/*.jar; do
    [ -f "$jar" ] || continue
    echo "Cleaning $(basename $jar)..."

    tmpdir=$(mktemp -d)

    unzip -qo "$jar" "gg/mmorealms/*" -d "$tmpdir" 2>/dev/null || true

    if [ -z "$(ls -A $tmpdir)" ]; then
      echo "  WARNING: nothing extracted from $(basename $jar), skipping repack"
      rm -rf "$tmpdir"
      continue
    fi

    # Generate path without creating the file first
    newjar="${tmpdir}_repacked.jar"
    (cd "$tmpdir" && zip -qr "$newjar" .)
    mv "$newjar" "$jar"

    rm -rf "$tmpdir"
  done
done

cypher-shell -u neo4j -p password -d system "CREATE OR REPLACE DATABASE neo4j" > /dev/null 2>&1

echo "Scanning..."
jqassistant scan

echo "Tagging artifacts..."
cypher-shell -u neo4j -p password "MATCH (a:Artifact) WHERE a.fileName CONTAINS '-fabric-' SET a:BACKEND" > /dev/null 2>&1
cypher-shell -u neo4j -p password "MATCH (a:Artifact) WHERE a.fileName CONTAINS '-velocity-' SET a:PROXY" > /dev/null 2>&1

echo "Done!"

# Example usage
: <<'END_COMMENT'
WITH 'gg.mmorealms'  AS packagePrefix,
     'join'          AS methodName,
     'BackendServer' AS declaringClassName,
     'PROXY'         AS groupFilter

MATCH (a:Artifact)-[:CONTAINS]->(startClass:Type)
                  -[:DECLARES]->(m:Method)
WHERE groupFilter IN labels(a)
  AND startClass.fqn STARTS WITH packagePrefix
  AND startClass.name = declaringClassName
  AND m.name = methodName

// Who calls the method (reverse only)
OPTIONAL MATCH callerPath = (callerClass:Type)-[:DECLARES]->(caller:Method)
                            -[:INVOKES*1..10]->(m)
WHERE callerClass.fqn STARTS WITH packagePrefix

OPTIONAL MATCH (startClass)-[:DECLARES]->(m)
OPTIONAL MATCH (callerClass)-[:DECLARES]->(caller)

RETURN callerPath, startClass, m, callerClass, caller
END_COMMENT



# See ALL
: <<'END_COMMENT'
MATCH (callerClass:Type)-[decl1:DECLARES]->(caller)-[inv:INVOKES]->(callee)
      <-[decl2:DECLARES]-(calleeClass:Type)
WHERE callerClass.fqn STARTS WITH 'gg.mmorealms'
  AND calleeClass.fqn STARTS WITH 'gg.mmorealms'
RETURN callerClass, decl1, caller, inv, callee, decl2, calleeClass
END_COMMENT