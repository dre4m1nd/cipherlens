/**
 * 前端路由管理
 **/

/** 引入需要权限的Modules */
import Dashboard from '../modules/dashboard'
import Password from '../modules/password'
import Log from '../modules/log'
import User from '../modules/user'

/** 登录后需要动态加入的本地路由 */
const FrontRoutes = [
  ...Dashboard,
  ...Password,
  ...Log,
  ...User,
]

export default FrontRoutes