import axios from 'axios';
// import useAuthStore from '../hooks/useAuthStore';

const axiosInstance = axios.create({
  baseURL: `${'http://localhost:8080'}`,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
  },
});

axiosInstance.interceptors.request.use(
  async (config) => {
    const token = localStorage.getItem('jwtToken');
    if (config.headers && token)
      config.headers.set(
        'Authorization',
        `Bearer 
     ${token}`
      );
    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    // if (error instanceof AxiosError && error.response?.status === 401) {
    //   useAuthStore.setState({ signedInAs: undefined });
    // }
    return Promise.reject(error);
  }
);

const redirectToLogin = () => {
    window.location.replace('/login');
};

export const api = {
    get: axiosInstance.get,
    post: axiosInstance.post,
    put: axiosInstance.put,
    delete: axiosInstance.delete,
};

export const authApi = {
    get: (url, config) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            redirectToLogin();
            return Promise.reject(new Error('User not authenticated'));
        }
        return axiosInstance.get(url, config);
    },
    post: (url, data, config) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            redirectToLogin();
            return Promise.reject(new Error('User not authenticated'));
        }
        return axiosInstance.post(url, data, config);
    },
    put: (url, data, config) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            redirectToLogin();
            return Promise.reject(new Error('User not authenticated'));
        }
        return axiosInstance.put(url, data, config);
    },
    delete: (url, config) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            redirectToLogin();
            return Promise.reject(new Error('User not authenticated'));
        }
        return axiosInstance.delete(url, config);
    },
    checkAuthentication: () => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            redirectToLogin();
            return Promise.reject(new Error('User not authenticated'));
        }
        return Promise.resolve();
    }
};

export default axiosInstance;
