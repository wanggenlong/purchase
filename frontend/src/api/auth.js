import request from '../utils/request';

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'post',
    data: { token: localStorage.getItem('token') }
  });
}