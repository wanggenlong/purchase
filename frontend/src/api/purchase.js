import request from '../utils/request';

export function getHealthStatus() {
  return request({
    url: '/health/check',
    method: 'post'
  });
}

export function getProducts() {
  return request({
    url: '/products/list',
    method: 'post'
  });
}

export function getPurchaseOrders() {
  return request({
    url: '/purchase-orders/list',
    method: 'post'
  });
}

export function generatePasswordHash(password) {
  return request({
    url: '/debug/hash',
    method: 'post',
    data: { password }
  });
}

export function verifyPassword(rawPassword, hashedPassword) {
  return request({
    url: '/debug/verify',
    method: 'post',
    data: { rawPassword, hashedPassword }
  });
}
