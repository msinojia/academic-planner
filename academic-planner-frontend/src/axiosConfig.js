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

export default axiosInstance;
