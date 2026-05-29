const currentUserId = window.APP_CONFIG?.currentUserId || 0;
const orderStart = window.APP_CONFIG?.orderStartTime || '';
const orderEnd = window.APP_CONFIG?.orderEndTime || '';
const storeId = window.APP_CONFIG?.storeId || '';

$.ajaxSetup({
    beforeSend: function(xhr) {
        const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
        if (match) xhr.setRequestHeader('X-XSRF-TOKEN', decodeURIComponent(match[1]));
    }
});

let cart = {};

// ========== UTILITY FUNCTIONS ==========
function isInOrderTime(start, end) {
    if (!start || !end) {
        console.warn('Invalid time format:', start, end);
        return false;
    }
    
    const now = new Date();
    const currentTime = now.getHours() * 60 + now.getMinutes();

    try {
        const [startHour, startMinute] = start.split(':').map(Number);
        const [endHour, endMinute] = end.split(':').map(Number);

        const startTime = startHour * 60 + startMinute;
        const endTime = endHour * 60 + endMinute;

        return currentTime >= startTime && currentTime <= endTime;
    } catch (error) {
        console.error('Error parsing time:', error);
        return false;
    }
}

// ========== ORDER TIME CHECKING ==========
let orderTimeInterval;

function checkOrderTime() {
    console.log('Checking order time... Current time:', new Date().toLocaleTimeString());
    console.log('Order time range:', orderStart, '-', orderEnd);
    
    const inOrderTime = isInOrderTime(orderStart, orderEnd);
    console.log('Is in order time:', inOrderTime);
    
    if (!inOrderTime) {
        console.log('Outside order time - disabling features');
        $('.btn-add, .btn-minus').prop('disabled', true);
        if ($('.order-time-alert').length === 0) {
            $('.detail-cart .title').after('<div class="order-time-alert">Hiện tại ngoài giờ đặt hàng</div>');
        }
        $('.product-item:not(.status-disabled)').addClass('time-disabled');
        
        // ẨN nút xóa và HIỆN nút ban
        $('.btn-delete').each(function() {
            const $deleteBtn = $(this);
            if (!$deleteBtn.siblings('.btn-ban').length && !$deleteBtn.hasClass('btn-ban')) {
                const $banBtn = $('<button type="button" class="btn btn-warning btn-sm ms-1 btn-ban"><i class="fa fa-ban" aria-hidden="true"></i></button>');
                $deleteBtn.replaceWith($banBtn);
            }
        });
        
        $('#confirm-btn').remove();
        stopPolling();
        
        if (orderTimeInterval) {
            clearInterval(orderTimeInterval);
            orderTimeInterval = null;
            console.log('Order time interval cleared');
        }
    } else {
        console.log('Inside order time - enabling features');
        $('.btn-add, .btn-minus').prop('disabled', false);
        $('.order-time-alert').remove();
        $('.product-item').removeClass('time-disabled');
        
        // Khôi phục nút delete từ nút ban
        $('.btn-ban').each(function() {
            const $banBtn = $(this);
            const $row = $banBtn.closest('tr');
            const itemId = $row.find('.btn-delete').data('id') || $banBtn.data('id');
            if (itemId) {
                const $deleteBtn = $('<button type="button" class="btn btn-danger btn-sm ms-1 btn-delete" data-id="' + itemId + '"><i class="fas fa-trash"></i></button>');
                $banBtn.replaceWith($deleteBtn);
            }
        });
        
        if (Object.keys(cart).length > 0 && $('#confirm-btn').length === 0) {
            $('.detail-cart .d-flex').append('<button id="confirm-btn" class="confirm-btn">Xác nhận</button>');
        }
        startPolling();
    }
}

// ========== POLLING FUNCTIONS ==========
let pollingInterval;
let isPollingActive = false;

function startPolling() {
    if (isPollingActive) {
        console.log('Polling already active');
        return;
    }
    
    const inOrderTime = isInOrderTime(orderStart, orderEnd);
    if (!inOrderTime) {
        console.log('Not starting polling - outside order time');
        return;
    }
    
    console.log('Starting polling...');
    isPollingActive = true;
    
    // Poll mỗi 10 giây
    pollingInterval = setInterval(function() {
        const stillInOrderTime = isInOrderTime(orderStart, orderEnd);
        if (stillInOrderTime) {
            console.log('Polling: loading ordered products');
            loadOrderedProducts();
        } else {
            console.log('Polling: outside order time, stopping');
            stopPolling();
            checkOrderTime(); // Bây giờ hàm này đã được định nghĩa
        }
    }, 10000);
}

function stopPolling() {
    if (pollingInterval) {
        console.log('Stopping polling...');
        clearInterval(pollingInterval);
        pollingInterval = null;
        isPollingActive = false;
    }
}

// Hàm load ordered products
function loadOrderedProducts() {
    // Kiểm tra trạng thái giờ đặt hàng trước khi load
    const inOrderTime = isInOrderTime(orderStart, orderEnd);
    console.log('Loading ordered products. In order time:', inOrderTime);
    
    $.ajax({
        url: '/orderItem/getByStore/' + storeId,
        type: 'GET',
        cache: false,
        success: function(response) {
            console.log('Ordered products loaded:', response);
            
            $('#ordered-products-table').empty();
            let totalQuantity = 0;
            
            if (Array.isArray(response) && response.length > 0) {
                response.forEach(function (item) {
                    totalQuantity += item.quantity;
                    
                    // Tự động quyết định hiển thị nút delete hay ban dựa trên giờ đặt hàng
                    const canDeleteByUser = item.userId === currentUserId;
                    const canDeleteByTime = inOrderTime;
                    
                    let actionButton;
                    if (canDeleteByUser && canDeleteByTime) {
                        actionButton = '<button type="button" class="btn btn-danger btn-sm ms-1 btn-delete" data-id="' + item.id + '"><i class="fas fa-trash"></i></button>';
                    } else {
                        actionButton = '<button type="button" class="btn btn-warning btn-sm ms-1 btn-ban"><i class="fa fa-ban" aria-hidden="true"></i></button>';
                    }
                    
                    var row = 
                        '<tr>' +
                            '<td data-label="Sản Phẩm" class="product-name">' + item.productName + '</td>' +
                            '<td data-label="Số Lượng" class="quantity"><span class="quantity-badge">' + item.quantity + '</span></td>' +
                            '<td data-label="Ghi chú" class="note"><span class="note">' + (item.note || '') + '</span></td>' +
                            '<td data-label="Người Đặt" class="customer-name">' + item.customerName + '</td>' +
                            '<td data-label="Thao tác">' + actionButton + '</td>' +
                        '</tr>';
                    $('#ordered-products-table').append(row);
                });
                
                const totalRow = 
                    '<tr class="font-bold bg-gray-100">' +
                        '<td colspan="1" class="text-left">Tổng cộng</td>' +
                        '<td colspan="1"><span class="quantity-badge">' + totalQuantity + '</span></td>' +
                        '<td colspan="3"></td>' +
                    '</tr>';
        
                $('#ordered-products-table').append(totalRow);
                
            } else {
                console.log('No ordered products found');
                $('#ordered-products-table').html(
                    '<tr>' +
                        '<td colspan="5" class="text-center">Chưa có sản phẩm nào được đặt</td>' +
                    '</tr>'
                );
            }
        },
        error: function(xhr, status, error) {
            console.error('Error loading ordered products:', error);
            $('#ordered-products-table').html(
                '<tr>' +
                    '<td colspan="5" class="text-center text-danger">Lỗi khi tải danh sách đơn hàng</td>' +
                '</tr>'
            );
        }
    });
}

function updateCart() {
    const $cartItems = $('#cart-items');
    $cartItems.empty();
    let total = 0;

    if (Object.keys(cart).length === 0) {
        $cartItems.html('<div class="empty-cart">Giỏ hàng trống</div>');
        $('#confirm-btn').remove();
    } else {
        $.each(cart, function(id, item) {
            const itemTotal = item.price * item.quantity;
            total += itemTotal;
            const priceFormatted = (item.price * item.quantity).toLocaleString('vi-VN');

            $cartItems.append(
                '<div class="cart-item" data-id="' + id + '">' +
                '<div class="cart-info">' +
                    '<div class="product-details">' +
                        '<div class="product-name">' + item.name + '</div>' +
                        '<div class="product-quality">Số lượng: ' + item.quantity + '</div>' +
                        '<div class="product-price">' + priceFormatted + ' đ</div>' +
                    '</div>' +
                    '<textarea class="note-input" placeholder="Ghi chú...">' + (item.note || '') + '</textarea>' +
                '</div>' +
                '<button class="btn-minus">-</button>' +
                '</div>'
            );
        });

        if ($('#confirm-btn').length === 0 && isInOrderTime(orderStart, orderEnd)) {
            $('.detail-cart .d-flex').append('<button id="confirm-btn" class="confirm-btn">Xác nhận</button>');
        }
    }

    $('#total').text(total.toLocaleString('vi-VN'));
}

$(document).ready(function() {
    console.log('Document ready, storeId:', storeId);
    console.log('Initial order time check:', isInOrderTime(orderStart, orderEnd));

    // Kiểm tra lần đầu
    checkOrderTime();

    // Chỉ bắt đầu interval nếu đang trong giờ đặt hàng
    if (isInOrderTime(orderStart, orderEnd)) {
        orderTimeInterval = setInterval(checkOrderTime, 60000);
        console.log('Interval started - inside order time');
    }

    // Kiểm tra khi người dùng quay lại tab
    $(window).on('focus', function() {
        console.log('Window focused, checking order time');
        checkOrderTime();
        if (isInOrderTime(orderStart, orderEnd) && !orderTimeInterval) {
            orderTimeInterval = setInterval(checkOrderTime, 60000);
            console.log('Interval restarted - inside order time');
        }
    });

    // Dừng polling khi rời tab
    $(window).on('blur', function() {
        console.log('Window blurred - stopping polling');
        stopPolling();
    });

    // Dọn dẹp interval khi rời trang
    $(window).on('beforeunload', function() {
        console.log('Cleaning up interval');
        stopPolling();
        if (orderTimeInterval) {
            clearInterval(orderTimeInterval);
            orderTimeInterval = null;
        }
    });

    // Event listeners
    $('#cart-items').on('input', '.note-input', function () {
        const id = $(this).closest('.cart-item').data('id');
        const note = $(this).val();
        if (cart[id]) {
            cart[id].note = note;
        }
    });

    $('.btn-add').on('click', function() {
        const $item = $(this).closest('.product-item');
        const id = $item.data('id');
        const name = $item.data('name');
        const price = parseInt($item.data('price'));

        if (cart[id]) {
            cart[id].quantity += 1;
        } else {
            cart[id] = { name, price, quantity: 1, note: '' };
        }

        updateCart();
    });

    $('#cart-items').on('click', '.btn-minus', function() {
        const id = $(this).closest('.cart-item').data('id');
        if (cart[id]) {
            cart[id].quantity -= 1;
            if (cart[id].quantity <= 0) {
                delete cart[id];
            }
            updateCart();
        }
    });

    // Nút xác nhận - sử dụng event delegation
    $(document).on('click', '#confirm-btn', function () {
        if (Object.keys(cart).length === 0) {
            alert('Giỏ hàng trống!');
            return;
        }

        let total = 0;
        const items = [];

        for (let id in cart) {
            const item = {
                productId: parseInt(id),
                quantity: cart[id].quantity,
                price: cart[id].price,
                note: cart[id].note || ''
            };
            items.push(item);
            total += item.price * item.quantity;
        }

        const orderData = {
            items: items,
            total: total,
            storeId: storeId
        };

        $.ajax({
            url: '/order/create',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(orderData),
            success: function (response) {
                cart = {};
                updateCart();
                console.log('Order created, waiting for polling update...');
                loadOrderedProducts();
            },
            error: function (xhr, status, error) {
                alert('Đặt hàng thất bại!');
                console.error('Order creation error:', error);
            }
        });
    });

    // Xử lý xóa order item
    $(document).on('click', '.btn-delete', function() {
        const $button = $(this);
        const id = $button.data('id');
        const $row = $button.closest('tr');
        
        if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
            $button.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i>');
            $row.css('opacity', '0.5');
            
            $.ajax({
                url: '/orderItem/delete/' + id,
                type: 'POST',
                success: function(response) {
                    console.log('Item deleted successfully');
                    $row.fadeOut(500, function() {
                        $(this).remove();
                        loadOrderedProducts();
                    });
                },
                error: function(xhr, status, error) {
                    $row.css('opacity', '1');
                    $button.prop('disabled', false).html('<i class="fas fa-trash"></i>');
                    alert('Xóa sản phẩm thất bại!');
                    console.error('Delete error:', error);
                }
            });
        }
    });

    // Load ordered products lần đầu
    loadOrderedProducts();
});