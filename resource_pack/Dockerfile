FROM nginx

RUN apt-get update
RUN apt-get install -y zip
COPY . /tmp/resource-pack
RUN mkdir -p /usr/share/nginx/html/
RUN cd /tmp/resource-pack && \
    zip -r resource-pack.zip . && \
    cp resource-pack.zip /usr/share/nginx/html/resource-pack.zip
