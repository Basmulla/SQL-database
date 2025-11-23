// Navbar.jsx
// Simple reusable navigation bar.

import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav style={{ display: "flex", gap: "20px", padding: "10px" }}>
      <Link to="/">Home</Link>
      <Link to="/login">Login</Link>
      <Link to="/register">Register</Link>
      <Link to="/orders">Orders</Link>
    </nav>
  );
}
