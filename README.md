# Raudikko Analysis for Elasticsearch

The Raudikko Analysis plugin provides Finnish language analysis using [Raudikko](https://github.com/EvidentSolutions/raudikko).

## Supported versions

Since 0.2, the plugin is built against Elasticsearch's [stable plugin API](https://www.elastic.co/guide/en/elasticsearch/plugins/current/plugin-api.html),
so a single package works with the Elasticsearch version it was built for and all later versions, instead of needing a
separate build per Elasticsearch version.

| Plugin version | Raudikko version | Elasticsearch versions |
|----------------|-------------------|-------------------------|
| 0.2            | 0.1.4             | 9.3.0 and later         |
| 0.1.2          | 0.1.2             | 8.x.x                   |
| 0.1.1          | 0.1.1             | 7.x.x                   |
| 0.1            | 0.1.1             | 7.3.2, 7.10.0           |

Need a version for Elasticsearch < 9.3.0? Check the [older releases](https://github.com/EvidentSolutions/elasticsearch-analysis-raudikko/releases), which still ship per-ES-version packages.

Looking for a version for OpenSearch? Check out [opensearch-analysis-raudikko](https://github.com/EvidentSolutions/opensearch-analysis-raudikko).

## Installing

Download the plugin zip from the [releases page](https://github.com/EvidentSolutions/elasticsearch-analysis-raudikko/releases) and install it with:

```
bin/elasticsearch-plugin install https://github.com/EvidentSolutions/elasticsearch-analysis-raudikko/releases/download/v0.2/elasticsearch-analysis-raudikko-0.2.zip
```

> [!NOTE]
> `elasticsearch-plugin install` runs as a short-lived tool with a small default heap, which can run out of memory
> while scanning the plugin's classes. If you hit an `OutOfMemoryError` during installation, retry with a larger
> heap: `CLI_JAVA_OPTS="-Xmx512m" bin/elasticsearch-plugin install ...`.

### Docker

Building the plugin and running it with Docker:
```
./gradlew build
cd etc
docker compose build
docker compose up
```
Elasticsearch should then be running and available at http://localhost:9200. Note that `docker compose up` is required
to publish the port — `docker compose run` does not publish ports unless you add `--service-ports`.

To build against a different (newer) Elasticsearch version, override the version on both the Gradle build and the
Docker build:
```
./gradlew build -DelasticsearchVersion=9.4.0
docker compose build --build-arg "ES_VERSION=9.4.0"
```

### Verify installation

After installing the plugin, you can quickly verify that it works by executing:

```
curl -XGET 'localhost:9200/_analyze' -H 'Content-Type: application/json' -d '
{
  "tokenizer" : "finnish",
  "filter" : [{"type": "raudikko"}],
  "text" : "Testataan raudikon analyysiä tällä tavalla yksinkertaisesti."
}'
```

If this works without error messages, you can proceed to configure the plugin index.

## Configuring

Include `finnish` tokenizer and `raudikko` filter in your analyzer, for example:

```json
{
  "index": {
    "analysis": {
      "analyzer": {
        "default": {
          "tokenizer": "finnish",
          "filter": ["lowercase", "raudikkoFilter"]
        }
      },
      "filter": {
        "raudikkoFilter": {
          "type": "raudikko"
        }
      }
    }
  }
}
```

You can use the following filter options to customize the behaviour of the filter:

| Parameter           | Default value | Description                                      |
|---------------------|---------------|--------------------------------------------------|
| analyzeAll          | true          | Use all analysis possibilities or just the first |
| splitCompoundWords  | false         | Split analysed compound words to its parts       |
| minimumWordSize     | 3             | minimum length of words to analyze               |
| maximumWordSize     | 100           | maximum length of words to analyze               |
| analysisCacheSize   | 1024          | number of analysis results to cache              |

## Compatibility with elasticsearch-analysis-voikko

This plugin supersedes [elasticsearch-analysis-voikko](https://github.com/EvidentSolutions/elasticsearch-analysis-voikko) and
is fully compatible with it (it provides a filter named `voikko` as a compatibility measure). Therefore, you can remove
the old plugin from your Elasticsearch installation and replace it with this plugin without having to reindex anything.
Just make sure to uninstall the old plugin `bin/elasticsearch-plugin remove elasticsearch-analysis-voikko` when installing
this one in place.

## License and copyright

Copyright (C) 2021-2026  Evident Solutions Oy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
