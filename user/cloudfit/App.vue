<script>
import userStore from './store/user.js'

export default {
	onLaunch: function() {
		console.log('App Launch')
		// 检查登录状态
		const token = uni.getStorageSync('token')
		if (token) {
			// 如果有 token，尝试获取用户信息
			userStore.fetchUserInfo().catch(err => {
				console.error('获取用户信息失败:', err)
				// 如果获取失败，清除 token
				userStore.logout()
			})
		}
	},
	onShow: function() {
		console.log('App Show')
	},
	onHide: function() {
		console.log('App Hide')
	}
}
</script>

<style>
/*每个页面公共css */
page {
	background-color: #f5f7fa;
	font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
}
</style>
