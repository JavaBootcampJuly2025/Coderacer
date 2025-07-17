
import { Routes, Route } from 'react-router-dom';
import Welcome from './Welcome';
import Register from './Register';
import Login from './Login';
import NotFound from './404';
import './App.css'
import Home from "./Home";
import LevelLoader from "./LevelLoader";
import Level from "./Level";

function App() {
    return (
        <Routes>
            <Route path="/" element={<Welcome />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/home" element={<Home />} />
            <Route path="/levelloader" element={<LevelLoader />} />
            <Route path="/level" element={<Level />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
  );
}
export default App
