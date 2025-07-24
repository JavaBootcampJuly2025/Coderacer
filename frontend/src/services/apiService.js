import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8000',
    withCredentials: true,
});

// Fetch account by ID
export const getAccountById = async (id, authToken) => {
    const response = await api.get(`/api/accounts/${id}`, {
        headers: {
            Authorization: `Bearer ${authToken}`,
        },
    });
    return response.data;
};

// Create a new account
export const createAccount = async accountData => {
    const response = await api.post('/api/accounts', accountData);
    return response.data;
};

// Delete an account
export const deleteAccount = async (id, authToken) => {
    const response = await api.delete(`/api/accounts/${id}`, {
        headers: {
            Authorization: `Bearer ${authToken}`,
        },
    });
    return response.data;
};

// Login into an account
export const login = async loginData => {
    const response = await api.post('/api/accounts/login', loginData);
    return response.data;
};

// Change password for an account
export const changePassword = async (id, passwordData, authToken) => {
    const response = await api.put(`/api/accounts/${id}/password`, passwordData, {
        headers: {
            Authorization: `Bearer ${authToken}`,
        },
    });
    return response.data;
}

// Fetch all levels
export const getAllLevels = async () => {
    const response = await api.get('/api/levels');
    return response.data;
};

// Create a new level session
export const createLevelSession = async (sessionData, authToken) => {
    const response = await api.post('/api/v1/level-sessions', sessionData, {
        headers: {
            Authorization: `Bearer ${authToken}`,
        },
    });;
    return response.data;
};

// Fetch account metrics
export const getAccountMetrics = async accountId => {
    const response = await api.get(`/api/v1/metrics/${accountId}`);
    return response.data;
};

// Get level by id
export const getLevelByid = async id => {
    const response = await api.get(`/api/levels/${id}`);
    return response.data;
};

// Get a random level with parameters
export const getRandomLevelWithParameters = async (language, difficulty) => {
    const response = await api.get('/api/levels/random', {
        params: {
            language,
            difficulty,
        }
    });
    return response.data;
};

// Fetch account gameplay metrics
export const getGameplayMetrics = async (id, authToken) => {
    const response = await api.get(`/api/metrics/gameplayMetrics/${id}`, {
        headers: {
            Authorization: `Bearer ${authToken}`,
        },
    });
    return response.data;
}

export const getRandomProblemWithDifficulty = async (difficulty) => {
    const response = await api.get(`/api/problems/random/difficulty/${difficulty}`);
    return response.data;
};
  
// Get top-rated accounts
export const getTop = async () => {
    const response = await api.get('/api/leaderboard/top');
    return response.data;
};

export const submitCode = async (id, code) => {
    const response = await api.post(`/api/test/problem/${id}`, { code });
    return response.data;
}