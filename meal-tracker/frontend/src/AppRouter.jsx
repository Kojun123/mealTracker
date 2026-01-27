import { Routes, Route } from "react-router-dom";
import Dashboard from "./Dashboard";
import Signup from "./Signup";
import Login from "./Login"; 

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/" element={<Dashboard />} />
    </Routes>
  );
}
