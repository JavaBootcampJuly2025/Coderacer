import { getAccountById } from "../services/apiService";
import {useEffect, useState} from 'react';

const useAccountInfo = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [rating, setRating] = useState(null);

    useEffect(() => {
        const getAccountInfo = async () => {
            const token = localStorage.getItem('loginToken');
            const id = localStorage.getItem('loginId');

            if(token == null || id == null) {
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
            }
        }

        getAccountInfo();
    }, []);

    return {
        username,
        email,
        rating
    };
};

export default useAccountInfo;