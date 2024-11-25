import os  # Add this import

from elasticsearch import Elasticsearch

# Initialize Elasticsearch client
es_client = Elasticsearch(
    os.getenv("ELASTICSEARCH_HOST"),
    timeout=60,
    http_auth=('elastic', 'J2AK2HFgS0*L4*IfAO1H')
)

def store_embedding(index_name, doc_id, embedding_vector, doc_metadata):
    """
    Store embedding in Elasticsearch for a given document.
    """
    print("Storing embedding for document ID:", doc_id)
    body = {
        "embedding": embedding_vector,
        "metadata": doc_metadata
    }
    es_client.index(index=index_name, id=doc_id, body=body)


def search_embeddings(index_name, query_vector):
    """
    Search for similar embeddings in Elasticsearch using cosine similarity.
    """
    script_query = {
        "script_score": {
            "query": {"match_all": {}},
            "script": {
                "source": "cosineSimilarity(params.query_vector, 'embedding') + 1.0",
                "params": {"query_vector": query_vector}
            }
        }
    }
    response = es_client.search(index=index_name, body={"query": script_query})
   
    print("Searching embeddings for index:", index_name)
    return response["hits"]["hits"]
