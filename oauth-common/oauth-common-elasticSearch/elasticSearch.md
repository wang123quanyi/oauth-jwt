## elasticSearch客户端：Java High Level REST Client

####关于@FieldInfo、FieldMapping、ElasticSearchUtils
````
基于：https://zhuanlan.zhihu.com/p/77813726
````


####关于Elasticsearch服务端
````
基于：https://www.cnblogs.com/chenqionghe/p/12496827.html
地址：https://www.elastic.co/cn/downloads/elasticsearch
````

####关于Elasticsearch服务端插件
~~~~
ik: https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.10.1/elasticsearch-analysis-ik-7.10.1.zip
pinyin: https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.10.1/elasticsearch-analysis-pinyin-7.10.1.zip
注意：Elasticsearch和分词插件需要版本匹配(相同) 
~~~~

####关于Elasticsearch可视化管理工具 kibana
````
地址：https://www.elastic.co/guide/en/kibana/current/targz.html
下载完成后修改config->kibana.yml文件：elasticsearch.hosts: ["http://localhost:9200"]指定es服务address
                                   i18n.locale: "zh-CN" 指定显示语言为中文

````

####关于Elasticsearch自定义分词(ik、拼音组合)
````
注意：自定义分词在Mapping的Setting部分设置
````