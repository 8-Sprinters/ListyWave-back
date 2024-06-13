ARG ELK_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/elasticsearch/elasticsearch:${ELK_VERSION}
RUN elasticsearch-plugin install analysis-nori
RUN bin/elasticsearch-plugin install https://github.com/netcrazy/elasticsearch-jaso-analyzer/releases/download/v7.16.2/jaso-analyzer-plugin-7.16.2-plugin.zip
# Add your elasticsearch plugins setup here
# Example: RUN elasticsearch-plugin install analysis-icu
