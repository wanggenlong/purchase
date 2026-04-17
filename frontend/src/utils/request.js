import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000
});

service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  error => Promise.reject(error)
);

service.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('realName');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default service;