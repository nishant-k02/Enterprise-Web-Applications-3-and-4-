from fastapi import FastAPI, HTTPException, Body
from openai_utils import create_product_data, create_product_reviews, generate_text_embeddings
from elastic_utils import store_embedding, search_embeddings
import json

app = FastAPI()

@app.post("/generate-product-records/")
async def generate_products_endpoint(product_category: str = Body(...)):
    """
    Generate 10 product records for a given category and store their embeddings in Elasticsearch.
    """
    try:
        # Generate product records using OpenAI
        product_records = create_product_data(product_category)
        product_list = json.loads(product_records)  # Parse the JSON string into a Python list of dictionaries

        for product in product_list:
            # Convert price to float, removing any currency symbols
            price_str = product.get("price", "").replace("$", "").strip()
            product["price"] = float(price_str) if price_str else 0.0

            # Combine product fields into a single text for embeddings
            product_text = f"{product['product_name']} {product['price']} {product['category']} {product['description']}"
            embedding_vector = generate_text_embeddings(product_text)

            # Store the embedding in Elasticsearch
            store_embedding(
                index_name="products",
                doc_id=product["product_name"],  # Use the product name as a unique ID
                embedding_vector=embedding_vector,
                doc_metadata={"product": product}
            )

        return {"products": product_list}  # Return the list of products for reference
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))


@app.post("/generate-reviews/")
async def generate_reviews_endpoint(product_title: str = Body(...)):
    """
    Generate 5 customer reviews for a specific product and store their embeddings in Elasticsearch.
    """
    try:
        # Generate reviews using OpenAI
        review_data = create_product_reviews(product_title)
        review_list = json.loads(review_data)  # Parse the JSON string into a Python list

        for i, review_entry in enumerate(review_list):
            # Assume each review entry is an object with `review` and `rating`
            p_name = review_entry["product_name"]
            review_text = review_entry["review"]
            rating = review_entry["rating"]

            # Combine product name, review, and rating into a single text for embedding
            review_text_for_embedding = f"Product Name: {p_name}\nReview: {review_text}\nRating: {rating}"

            # Generate embedding for the combined text
            embedding_vector = generate_text_embeddings(review_text_for_embedding)

            # Store the embedding in Elasticsearch
            store_embedding(
                index_name="reviews",
                doc_id=f"{p_name}_review_{i}",  # Use product name and review index as unique ID
                embedding_vector=embedding_vector,
                doc_metadata={
                    "product_name": p_name,
                    "review": review_text,
                    "rating": rating,
                }
            )

        return {"reviews": review_list}  # Return the list of reviews for reference
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))


@app.post("/search-reviews/")
async def search_reviews_endpoint(user_query: str = Body(...)):
    """
    Search for reviews similar to a user query using semantic embeddings.
    """
    try:
        print(f"Searching reviews for query: {user_query}")

        # Generate query embedding using OpenAI
        query_embedding_vector = generate_text_embeddings(user_query)

        # Search in Elasticsearch index 'reviews'
        search_results = search_embeddings("reviews", query_embedding_vector)

        # Extract metadata from search results
        matched_reviews = [
            {
                "product_name": hit["_source"]["metadata"]["product_name"],
                "review": hit["_source"]["metadata"]["review"],
                "rating": hit["_source"]["metadata"]["rating"],
                "score": hit["_score"]
            }
            for hit in search_results
        ]

        # Return matched reviews
        return {"matches": matched_reviews}
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))


@app.post("/recommend-product/")
async def recommend_product_endpoint(user_query: str = Body(...)):
    """
    Recommend products similar to a user query using semantic embeddings.
    """
    try:
        print(f"Recommending products for query: {user_query}")

        # Generate query embedding using OpenAI
        query_embedding_vector = generate_text_embeddings(user_query)

        # Search in Elasticsearch index 'products'
        search_results = search_embeddings("products", query_embedding_vector)

        # Extract metadata from search results
        product_recommendations = [
            {
                "product_name": hit["_source"]["metadata"]["product"]["product_name"],
                "price": hit["_source"]["metadata"]["product"]["price"],
                "category": hit["_source"]["metadata"]["product"]["category"],
                "description": hit["_source"]["metadata"]["product"]["description"],
                "score": hit["_score"]
            }
            for hit in search_results
        ]

        # Return recommendations
        return {"recommendations": product_recommendations}
    except Exception as error:
        raise HTTPException(status_code=500, detail=str(error))
