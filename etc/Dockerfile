ARG ES_VERSION=9.3.0
FROM docker.elastic.co/elasticsearch/elasticsearch:${ES_VERSION}
# Needs to repeat the ARG here or it won't work
# https://docs.docker.com/engine/reference/builder/#understand-how-arg-and-from-interact
ARG ES_VERSION
COPY /build/distributions/elasticsearch-analysis-raudikko-0.2-es${ES_VERSION}.zip /install/elasticsearch-analysis-raudikko.zip
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install \
    --batch file:///install/elasticsearch-analysis-raudikko.zip \
