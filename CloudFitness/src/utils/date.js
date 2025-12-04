import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import duration from 'dayjs/plugin/duration'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.extend(duration)
dayjs.locale('zh-cn')

export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(date).format(format)
}

export const formatRelativeTime = (date) => {
  return dayjs(date).fromNow()
}

export const isExpired = (date) => {
  return dayjs(date).isBefore(dayjs())
}

export const getTimeRemaining = (date) => {
  const now = dayjs()
  const target = dayjs(date)
  const diff = target.diff(now)
  
  if (diff <= 0) return '已过期'
  
  const duration = dayjs.duration(diff)
  const days = Math.floor(duration.asDays())
  const hours = duration.hours()
  const minutes = duration.minutes()
  
  if (days > 0) return `${days}天${hours}小时`
  if (hours > 0) return `${hours}小时${minutes}分钟`
  return `${minutes}分钟`
}

export default dayjs

