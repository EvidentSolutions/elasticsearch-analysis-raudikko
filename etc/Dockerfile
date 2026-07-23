ARG ES_VERSION=9.3.0
FROM docker.elastic.co/elasticsearch/elasticsearch:${ES_VERSION}
# Needs to repeat the ARG here or it won't work
# https://docs.docker.com/engine/reference/builder/#understand-how-arg-and-from-interact
ARG ES_VERSION
COPY /build/distributions/elasticsearch-analysis-raudikko-0.2.zip /install/
# elasticsearch-plugin runs as a short-lived CLI JVM with a hardcoded 64m heap
# (see bin/elasticsearch-cli). The stable plugin API's NamedComponent class
# scanner reads every class in the plugin's jars into memory, which overflows
# that default heap, so bump it via CLI_JAVA_OPTS.
RUN CLI_JAVA_OPTS="-Xmx512m" /usr/share/elasticsearch/bin/elasticsearch-plugin install \
    --batch file:///install/elasticsearch-analysis-raudikko-0.2.zip
