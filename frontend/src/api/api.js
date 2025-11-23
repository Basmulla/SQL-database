// api.js
// Centralized API helper using axios.

import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api"
});

// USERS
export const registerUser = (data) => API.post("/users/register", data);
export const loginUser = (data) => API.post("/users/login", data);

// ORDERS
export const getUserOrders = (userId) => API.get(`/orders/user/${userId}`);
export const createOrder = (data) => API.post("/orders", data);

export default API;
