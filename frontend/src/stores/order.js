import { defineStore } from 'pinia'
import { orderApi } from '@/utils/mockUtils'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: [],
    currentOrder: null,
    loading: false,
    total: 0
  }),

  actions: {
    async fetchOrders(params) {
      this.loading = true
      try {
        const res = await orderApi.list(params)
        this.orders = res.records
        this.total = res.total
        return res
      } catch (error) {
        console.error('获取订单列表失败:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createOrder(orderData) {
      try {
        const res = await orderApi.create(orderData)
        // 更新订单列表
        if (res) {
          this.orders.unshift(res)
          this.total += 1
        }
        return res
      } catch (error) {
        console.error('创建订单失败:', error)
        throw error
      }
    },

    async updateOrder(orderData) {
      try {
        const res = await orderApi.update(orderData)
        // 更新订单列表中的数据
        if (res) {
          const index = this.orders.findIndex(order => order.id === res.id)
          if (index !== -1) {
            this.orders[index] = res
          }
        }
        return res
      } catch (error) {
        console.error('更新订单失败:', error)
        throw error
      }
    },

    async deleteOrder(orderId) {
      try {
        await orderApi.delete(orderId)
        // 从列表中移除
        const index = this.orders.findIndex(order => order.id === orderId)
        if (index !== -1) {
          this.orders.splice(index, 1)
          this.total -= 1
        }
      } catch (error) {
        console.error('删除订单失败:', error)
        throw error
      }
    },

    async fetchOrderDetail(orderId) {
      try {
        const res = await orderApi.detail(orderId)
        this.currentOrder = res
        return res
      } catch (error) {
        console.error('获取订单详情失败:', error)
        throw error
      }
    }
  }
}) 