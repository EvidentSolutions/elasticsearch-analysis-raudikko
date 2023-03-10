ARG ES_VERSION=8.5.3
ARG OS_VERSION=2.5.0

FROM gradle as gradle-base

WORKDIR /home/gradle/project
COPY . .

FROM gradle-base as build-for-elasticsearch

ARG ES_VERSION
RUN mv elasticsearch/* .
RUN rm -rf elasticsearch opensearch
RUN gradle build -DelasticsearchVersion=${ES_VERSION}
RUN mv /home/gradle/project/build/distributions/elasticsearch-analysis-raudikko-0.1-es${ES_VERSION}.zip /dist.zip

FROM gradle-base as build-for-opensearch

ARG OS_VERSION
RUN mv opensearch/* .
RUN rm -rf elasticsearch opensearch
RUN gradle build -DopensearchVersion=${OS_VERSION}
RUN mv /home/gradle/project/build/distributions/opensearch-analysis-raudikko-0.1-os${OS_VERSION}.zip /dist.zip

WORKDIR /home/gradle/project

FROM elasticsearch:${ES_VERSION} as elasticsearch

COPY --from=build-for-elasticsearch /dist.zip /elasticsearch-analysis-raudikko.zip

RUN elasticsearch-plugin install --batch file:///elasticsearch-analysis-raudikko.zip

FROM opensearchproject/opensearch:${OS_VERSION} as opensearch

COPY --from=build-for-opensearch /dist.zip /opensearch-analysis-raudikko.zip

RUN opensearch-plugin install --batch file:///opensearch-analysis-raudikko.zip
