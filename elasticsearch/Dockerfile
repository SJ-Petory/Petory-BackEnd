# docker-compose.yml에 명시된 이미지와 동일한 버전을 사용합니다.
FROM docker.elastic.co/elasticsearch/elasticsearch:8.13.4

# nori 형태소 분석기 플러그인을 설치합니다.
RUN bin/elasticsearch-plugin install analysis-nori