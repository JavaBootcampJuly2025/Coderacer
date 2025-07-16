import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8000',
    withCredentials: true,
});

// Fetch all accounts
export const getAllAccounts = async () => {
    const response = await api.get('/api/accounts');
    return response.data;
};

// Fetch account by ID
export const getAccountById = async id => {
    const response = await api.get(`/api/accounts/${id}`);
    return response.data;
};

// Create a new account
export const createAccount = async accountData => {
    const response = await api.post('/api/accounts', accountData);
    return response.data;
};

// Fetch all levels
export const getAllLevels = async () => {
    const response = await api.get('/api/levels');
    return response.data;
};

// Create a new level session
export const createLevelSession = async sessionData => {
    const response = await api.post('/api/v1/level-sessions', sessionData);
    return response.data;
};

// Fetch account metrics
export const getAccountMetrics = async accountId => {
    const response = await api.get(`/api/v1/metrics/${accountId}`);
    return response.data;
};
