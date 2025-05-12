// 模拟数据
const mockData = {
  // 模拟分类列表
  categories: [
    { id: 1, name: '人物形象', status: '1', createTime: '2023-06-01 10:00:00' },
    { id: 2, name: '动物形象', status: '1', createTime: '2023-06-02 10:00:00' },
    { id: 3, name: '卡通形象', status: '1', createTime: '2023-06-03 10:00:00' }
  ],
  
  // 模拟形象列表
  templates: [
    { 
      id: 1, 
      name: '默认形象1', 
      categoryId: 1, 
      categoryName: '人物形象',
      description: '这是一个默认的人物形象', 
      previewUrl: 'https://via.placeholder.com/200',
      status: '1',
      createTime: '2023-06-10 10:00:00'
    },
    { 
      id: 2, 
      name: '默认形象2', 
      categoryId: 2, 
      categoryName: '动物形象',
      description: '这是一个默认的动物形象', 
      previewUrl: 'https://via.placeholder.com/200',
      status: '1',
      createTime: '2023-06-11 10:00:00'
    }
  ]
}

// 确认模拟数据是否正确加载
console.log('模拟数据已加载:', mockData);

// 模拟请求处理函数
export function mockRequest(url, method, params, data) {
  console.log('模拟请求处理:', { url, method, params, data });
  
  // 处理分类相关请求
  if (url.includes('/template/category/list')) {
    console.log('返回模拟分类数据');
    return {
      code: 200,
      message: 'success',
      data: mockData.categories
    }
  }
  
  // 处理形象列表请求
  if (url.includes('/template/list')) {
    console.log('返回模拟形象数据');
    return {
      code: 200,
      message: 'success',
      data: {
        records: mockData.templates,
        total: mockData.templates.length,
        size: params?.size || 10,
        current: params?.page || 1,
        pages: 1
      }
    }
  }
  
  // 默认返回
  console.log('未找到匹配的模拟数据，返回404');
  return {
    code: 404,
    message: '接口不存在',
    data: null
  }
} 