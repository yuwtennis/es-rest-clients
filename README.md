# OVERVIEW

This project include sample coedes for implementation of rest client to access elasticsearch.

# Environment

Elasticsearch cluster was built upon minikube.
Cluster was built using below materials.

1.[Custom Resource Definition](https://www.elastic.co/guide/en/cloud-on-k8s/current/k8s-quickstart.html)

~~~
kubectl apply -f https://download.elastic.co/downloads/eck/1.0.1/all-in-one.yaml
~~~

2.Helm Chart

https://github.com/yuwtennis/chart-elastic.git

