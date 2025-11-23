const BASE_URL = "http://localhost:8080"; // backend will replace this

// Example login API call
export async function loginUser(data) {
    const response = await fetch(BASE_URL + "/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    return response.json();
}

// Example register API call
export async function registerUser(data) {
    const response = await fetch(BASE_URL + "/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    return response.json();
}

// Example place order (your database project)
export async function placeOrder(data) {
    const response = await fetch(BASE_URL + "/orders", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    return response.json();
}
