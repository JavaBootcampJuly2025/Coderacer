
import { Routes, Route } from 'react-router-dom';
import Home from './Home';
import Register from './Register';
import Login from './Login';
import NotFound from './404';
import LevelLoader from './LevelLoader';
import Level from './Level';
import './App.css';

function App() {
    return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />
      <Route path="/load" element={<LevelLoader />} />
      <Route path="/play" element={<Level />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
export default App
