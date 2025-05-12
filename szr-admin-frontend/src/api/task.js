import request from '@/utils/request'

// 获取任务列表
export function getTaskList(params) {
  return request({
    url: '/api/task/list',
    method: 'get',
    params
  })
}

// 获取任务详情
export function getTaskDetail(id) {
  return request({
    url: `/api/task/${id}`,
    method: 'get'
  })
}

// 创建任务
export function createTask(data) {
  return request({
    url: '/api/task',
    method: 'post',
    data
  })
}

// 添加任务（与createTask相同，为兼容现有代码）
export function addTask(data) {
  return request({
    url: '/api/task',
    method: 'post',
    data
  })
}

// 取消任务
export function cancelTask(id) {
  return request({
    url: `/api/task/${id}/cancel`,
    method: 'put'
  })
}

// 删除任务
export function deleteTask(id) {
  return request({
    url: `/api/task/${id}`,
    method: 'delete'
  })
}

// 获取任务结果
export function getTaskResult(id) {
  return request({
    url: `/api/task/${id}/result`,
    method: 'get'
  })
}

// 更新任务状态
export function updateTaskStatus(id, status) {
  return request({
    url: `/api/task/${id}/status`,
    method: 'put',
    params: {
      status
    }
  })
} 