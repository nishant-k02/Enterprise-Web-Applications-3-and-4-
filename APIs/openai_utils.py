import os
from openai import OpenAI
from dotenv import load_dotenv

# Load environment configurations
load_dotenv()

# Initialize OpenAI client
openai_client = OpenAI(
    api_key="your_openAI_key",
    timeout=60  # Set timeout duration to 60 seconds
)

# EMBEDDING_MODEL_NAME = "text-embedding-ada-002"  # Example of an embedding model
EMBEDDING_MODEL_NAME = "text-embedding-3-small"  # Using another valid embedding model


def create_product_data(product_category):
    """
    Generate product records for a given category.
    Each record includes: Name, Price, Category, Description.
    Returns a list of product dictionaries.
    """
    prompt_text = (
        f"Generate 10 concise Smart Home product data for the category '{product_category}'. "
        "For each product, provide the product_name, price, category, and a description (max 100 words) attributes"
        "Return the products in JSON format so that I can convert it into object."
    )
    response = openai_client.chat.completions.create(
        messages=[
            {"role": "system", "content": "You are a product record generator."},
            {"role": "user", "content": prompt_text},
        ],
        model="gpt-4",
        max_tokens=1500,
    )
    print("OpenAI generated product data: ", response.choices[0].message.content)
    return response.choices[0].message.content  # Extract the generated content


def create_product_reviews(product_title):
    """
    Generate 5 customer reviews for a specific product.
    Returns a list of review strings.
    """
    prompt_text = (
        f"Write 5 customer reviews for the different models of SmartHome products for '{product_title}'. "
        "Each review should be 40 words max, having product_name, review, rating attributes. "
        "Return the reviews in JSON format so that I can convert it into object."
    )
    response = openai_client.chat.completions.create(
        messages=[
            {"role": "system", "content": "You are a product review generator. also make sure to have 3 positive and 2 negative reviews total 5 reviews"},
            {"role": "user", "content": prompt_text},
        ],
        model="gpt-4",
        max_tokens=500,
    )
    return response.choices[0].message.content  # Extract the generated reviews


def generate_text_embeddings(input_text):
    """
    Generate embeddings for the given text using OpenAI's embedding API.
    Returns a list of float values representing the embedding vector.
    """
    print("Generating embeddings for:", input_text)
    response = openai_client.embeddings.create(
        input=input_text,
        model=EMBEDDING_MODEL_NAME
    )
    return response.data[0].embedding  # Extract the embedding vector
