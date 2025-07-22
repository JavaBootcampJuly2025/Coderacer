import { getAccountById, getGameplayMetrics } from "../services/apiService";
import {useEffect, useState} from 'react';

const useAccountInfo = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [rating, setRating] = useState(null);
    const [loggedOn, setLoggedOn] = useState(false);
    const [avgCpm, setAvgCpm] = useState(0);
    const [avgAccuracy, setAvgAccuracy] = useState(0);

    useEffect(() => {
        const getAccountInfo = async () => {
            const token = localStorage.getItem('loginToken');
            const id = localStorage.getItem('loginId');

            if(token == null || id == null) {
                setLoggedOn(false);
                return;
            }
            
            try {
                const response = await getAccountById(id, token);
                setUsername(response.username);
                setEmail(response.email);
                setRating(response.rating);
            } catch (error) {
                localStorage.removeItem('loginToken');
                localStorage.removeItem('loginId');
                setLoggedOn(false);
                return;
            }

            setLoggedOn(true);

            try {
                const response = await getGameplayMetrics(id, token);
                setAvgCpm(response.avgCpm);
                setAvgAccuracy(response.avgAccuracy);
            } catch (error) {

            }
        }

        getAccountInfo();
    }, []);

    return {
        username,
        email,
        rating,
        loggedOn,
        avgCpm,
        avgAccuracy,
    };
};

export default useAccountInfo;