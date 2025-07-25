import { Routes, Route } from 'react-router-dom';
import Register from './pages/Register';
import Login from './pages/Login';
import NotFound from './404';
import './App.css';
import Home from './pages/Home';
import Level from './pages/Level';
import GameMode from './pages/GameMode';

function App() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/home" element={<Home />} />
            <Route path="/level" element={<Level />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="*" element={<NotFound />} />
            <Route path="/gamemode" element={<GameMode />} />
        </Routes>
    );
}

export default App;