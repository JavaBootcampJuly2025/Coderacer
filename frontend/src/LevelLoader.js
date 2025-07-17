import { useEffect } from "react";
import { useSearchParams, useNavigate  } from "react-router-dom";
import { getLevelByid } from './services/apiService';


function LevelLoader() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();


    useEffect(() => {
        const levelId = searchParams.get("id");

        async function loadLevel() {
            try {
                const levelData = await getLevelByid(levelId);
                navigate("/play", { state: { level: levelData } });
            } catch (error) {
            }
        }

        loadLevel();
    }, [searchParams]);

    return <p>Loading level...</p>;
}

export default LevelLoader;