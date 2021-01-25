# Raudikko Analysis for Elasticsearch

The Raudikko Analysis plugin provides Finnish language analysis using [Raudikko](https://github.com/EvidentSolutions/raudikko).

## Supported versions

| Plugin version | Raudikko version | Elasticsearch versions |
| -------------- | ---------------- | ---------------------- |
| 0.1            | 0.1.1            | 7.3.2, 7.10.0          |

If you are not installing the latest version, follow the links in the table to see installation instructions for the old version.

## Installing

To install the plugin, run the command depending on your ES installation: 

### Elasticsearch 7.3.2

```
bin/elasticsearch-plugin install https://github.com/EvidentSolutions/elasticsearch-analysis-raudikko/releases/download/v0.1/elasticsearch-analysis-raudikko-0.1-es7.3.2.zip
```

### Elasticsearch 7.10.0

```
bin/elasticsearch-plugin install https://github.com/EvidentSolutions/elasticsearch-analysis-raudikko/releases/download/v0.1/elasticsearch-analysis-raudikko-0.1-es7.10.0.zip
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

| Parameter         | Default value    | Description                                      |
|-------------------|------------------|--------------------------------------------------|
| analyzeAll        | true             | Use all analysis possibilities or just the first |
| minimumWordSize   | 3                | minimum length of words to analyze               |
| maximumWordSize   | 100              | maximum length of words to analyze               |
| analysisCacheSize | 1024             | number of analysis results to cache              |

## Compatibility with elasticsearch-analysis-voikko

This plugin supersedes [elasticsearch-analysis-voikko](https://github.com/EvidentSolutions/elasticsearch-analysis-voikko) and
is fully compatible with it (it provides a filter named `voikko` as a compatibility measure). Therefore, you can remove
the old plugin from your Elasticsearch installation and replace it with this plugin without having to reindex anything.
Just make sure to uninstall the old plugin `bin/elasticsearch-plugin remove elasticsearch-analysis-voikko` when installing
this one in place.

## License and copyright
 
Copyright (C) 2021  Evident Solutions Oy

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
