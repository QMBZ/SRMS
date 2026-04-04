// 解析 JWT 获取 payload
export const parseToken = (token) => {
  try {
    if (!token || token.split('.').length !== 3) return null
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join(''),
    )
    return JSON.parse(jsonPayload)
  } catch (err) {
    return null
  }
}

// 获取用户 ID
export const getUserId = (token) => {
  const payload = parseToken(token)
  return payload?.userId || null
}

// 获取角色 ID
export const getRoleId = (token) => {
  const payload = parseToken(token)
  return payload?.roleId || null
}
