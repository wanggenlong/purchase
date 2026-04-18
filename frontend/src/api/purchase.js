import request from '../utils/request';

export function getHealthStatus() {
  return request({
    url: '/health/check',
    method: 'post'
  });
}

export function getProductPage(data) {
  return request({
    url: '/products/page',
    method: 'post',
    data
  });
}

export function addProduct(data) {
  return request({
    url: '/products/add',
    method: 'post',
    data
  });
}

export function updateProduct(data) {
  return request({
    url: '/products/update',
    method: 'post',
    data
  });
}

export function deleteProduct(data) {
  return request({
    url: '/products/delete',
    method: 'post',
    data
  });
}

export function exportProduct(data) {
  return request({
    url: '/products/export',
    method: 'post',
    data,
    responseType: 'blob'
  });
}

export function getCategoryTree() {
  return request({
    url: '/categories/tree',
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
