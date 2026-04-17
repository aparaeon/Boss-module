SOURCE=$1

mkdir -p $SOURCE-merged

find common -type f | while read -r file; do
    relative_path="${file#common/}"
    output_file="merged/$relative_path"
    mkdir -p "$(dirname "$output_file")"

    if [[ "$file" == *.json ]]; then
        if [ -f "$SOURCE/$relative_path" ]; then
            jq -s 'reduce .[] as $item ({}; . * $item)' "$file" "$SOURCE/$relative_path" > "$output_file"
        else
            cp "$file" "$output_file"
        fi
    else
        if [ ! -f "$SOURCE/$relative_path" ]; then
            cp "$file" "$output_file"
        fi
    fi
done

find $SOURCE -type f | while read -r file; do
    relative_path="${file#$SOURCE/}"
    output_file="merged/$relative_path"
    if [ ! -f "$output_file" ]; then
        mkdir -p "$(dirname "$output_file")"
        cp "$file" "$output_file"
    fi
done
