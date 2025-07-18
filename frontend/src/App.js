import { Routes, Route } from 'react-router-dom';
import Register from './pages/Register';
import Login from './pages/Login';
import NotFound from './404';
import './App.css';
import Home from './pages/Home';
import LevelLoader from './pages/LevelLoader';
import Level from './pages/Level';

function App() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/home" element={<Home />} />
            <Route path="/levelloader" element={<LevelLoader />} />
            <Route path="/level" element={<Level />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    );
}

export default App;