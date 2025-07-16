import axios from 'axios';

const API_URL = '/api/accounts';

export const getAllAccounts = async () => {
    const response = await axios.get(API_URL);
    return response.data;
};

export const getAccountById = async (id) => {
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
};

export const createAccount = async (account) => {
    const response = await axios.post(API_URL, account);
    return response.data;
};