// Login.jsx
// Simple login form

import { useState } from "react";
import Navbar from "../components/Navbar";
import { loginUser } from "../api/api";

export default function Login() {
  const [form, setForm] = useState({ username: "", password: "" });

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const res = await loginUser(form);
      alert("Login successful!");
      console.log(res.data);
    } catch (err) {
      alert("Login failed");
      console.error(err);
    }
  }

  return (
    <div>
      <Navbar />
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <input
          placeholder="Username"
          onChange={(e) => setForm({ ...form, username: e.target.value })}
        /><br />

        <input
          type="password"
          placeholder="Password"
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        /><br />

        <button>Login</button>
      </form>
    </div>
  );
}
