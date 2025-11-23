// Orders.jsx
// Displays all orders for a user

import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { getUserOrders } from "../api/api";

export default function Orders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    async function load() {
      try {
        const res = await getUserOrders(1); // TEMP user ID
        setOrders(res.data);
      } catch (err) {
        console.error(err);
      }
    }
    load();
  }, []);

  return (
    <div>
      <Navbar />
      <h2>Your Orders</h2>

      {orders.length === 0 && <p>No orders found.</p>}

      {orders.map((order) => (
        <div
          key={order.id}
          style={{
            border: "1px solid #ccc",
            padding: "10px",
            marginBottom: "10px",
            borderRadius: "6px"
          }}
        >
          <strong>Order #{order.id}</strong>
          <p>Item: {order.itemName}</p>
          <p>Price: ${order.price}</p>
        </div>
      ))}
    </div>
  );
}
