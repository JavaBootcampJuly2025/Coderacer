
import { Routes, Route } from 'react-router-dom';
import Welcome from './Welcome';
import Register from './Register';
import Login from './Login';
import NotFound from './404';
import './App.css'
import Home from "./Home";

function App() {
    return (
    <Routes>
      <Route path="/" element={<Welcome />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route path="/home" element={<Home />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
export default App
