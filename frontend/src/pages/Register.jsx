// Register.jsx
// Simple registration form

import { useState } from "react";
import Navbar from "../components/Navbar";
import { registerUser } from "../api/api";

export default function Register() {
  const [form, setForm] = useState({ username: "", password: "" });

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      await registerUser(form);
      alert("Account created!");
    } catch (err) {
      alert("Registration failed");
      console.error(err);
    }
  }

  return (
    <div>
      <Navbar />
      <h2>Create Account</h2>

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

        <button>Register</button>
      </form>
    </div>
  );
}
