import request from '@/utils/request'

// 查询专利库数据列表
export function listLibrary(query) {
  return request({
    url: '/patent/library/list',
    method: 'get',
    params: query
  })
}

// 查询专利库数据详细
export function getLibrary(id) {
  return request({
    url: '/patent/library/' + id,
    method: 'get'
  })
}

// 新增专利库数据
export function addLibrary(data) {
  return request({
    url: '/patent/library',
    method: 'post',
    data: data
  })
}

// 修改专利库数据
export function updateLibrary(data) {
  return request({
    url: '/patent/library',
    method: 'put',
    data: data
  })
}

// 删除专利库数据
export function delLibrary(id) {
  return request({
    url: '/patent/library/' + id,
    method: 'delete'
  })
}
