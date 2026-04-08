// Common functions for list pages (users, stores, products, orders)
document.addEventListener('DOMContentLoaded', function () {
    const selectAll = document.getElementById('selectAll');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const deleteSelectedBtn = document.getElementById('deleteSelectedBtn');
    const selectedItemIds = document.getElementById('selectedItemIds');

    if (itemCheckboxes.length > 0) {
        // Handle "Select All" checkbox
        selectAll.addEventListener('change', function () {
            console.log('Select All changed:', this.checked);
            itemCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateDeleteButton();
        });

        // Handle individual checkboxes
        itemCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function () {
                console.log('Checkbox changed:', this.value, this.checked);
                updateDeleteButton();
            });
        });

        // Update delete button state
        function updateDeleteButton() {
            const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
            console.log('Checked boxes:', checkedBoxes.length);
            deleteSelectedBtn.disabled = checkedBoxes.length === 0;
            selectAll.checked = checkedBoxes.length === itemCheckboxes.length;
        }

        // Handle delete selected button
        deleteSelectedBtn.addEventListener('click', function () {
            console.log('Delete Selected clicked');
            const checkedBoxes = document.querySelectorAll('.item-checkbox:checked');
            const itemIds = Array.from(checkedBoxes).map(cb => cb.value);
            selectedItemIds.value = itemIds.join(',');

            const deleteModal = document.getElementById('deleteMultipleItemsModal');
            if (deleteModal) {
                const form = deleteModal.querySelector('form');
                const input = form.querySelector('input[name="itemIds"]');
                if (input) {
                    input.value = itemIds.join(',');
                    console.log('Setting selected IDs in form:', input.value);
                }
                new bootstrap.Modal(deleteModal).show();
            } else {
                console.error('Delete modal not found!');
            }
        });
    }

    // Handle delete button for individual items
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const itemId = this.dataset.itemId;
            console.log('Delete button clicked, data-item-id:', itemId);
            console.log('Button dataset:', this.dataset);

            if (!itemId) {
                console.error('No ID found for delete button');
                return;
            }

            const modal = document.getElementById('deleteItemModal');
            console.log('Found modal:', modal);

            if (modal) {
                const itemIdSpan = modal.querySelector('#deleteItemId');
                console.log('Found itemIdSpan:', itemIdSpan);

                if (itemIdSpan) {
                    itemIdSpan.textContent = itemId;
                    console.log('Updated item ID in modal:', itemId);
                }

                const form = modal.querySelector('form');
                console.log('Found form:', form);
                if (form) {
                    // Update form action to include the ID in the URL path
                    form.action = form.action + itemId;
                    console.log('Updated form action:', form.action);
                }

                new bootstrap.Modal(modal).show();
            } else {
                console.error('Delete modal not found!');
            }
        });
    });
});

$(document).ready(function ($) {
    let currentStoreId = null;
    // Xử lý khi click vào nút crawl data
    $('.craw-data').click(function (e) {
        e.preventDefault();
        currentStoreId = $(this).data('store-id');
        $('#storeIdInput').val(currentStoreId);
        $('#urlInput').val('');
        $('#crawlResult').hide().empty();
        $('#crawlModal').modal('show');
    });

    // Xử lý khi click nút bắt đầu crawl
    $('#startCrawlBtn').click(function () {
        const url = $('#urlInput').val().trim();

        if (!url) {
            alert('Vui lòng nhập URL GrabFood');
            return;
        }

        // Hiển thị loading
        $('#startCrawlBtn').prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...');

        // Gọi API crawl
        $.ajax({
            url: '/crawl/store',
            type: 'POST',
            contentType: 'application/json',
            timeout: 210000, // 3.5 phút — lớn hơn server timeout (3 phút)
            data: JSON.stringify({
                storeId: currentStoreId,
                url: url
            }),
            success: function (response) {
                const count = response.productsCount || 0;
                $('#startCrawlBtn').prop('disabled', false).html('Bắt đầu Crawl');

                if (count > 0) {
                    const message = response.message || 'Crawl dữ liệu thành công';
                    $('#crawlResult').show().html(
                        '<div class="alert alert-success">' +
                        '<h6>' + message + '</h6>' +
                        '<p>Đã thêm ' + count + ' sản phẩm vào cửa hàng.</p>' +
                        '</div>'
                    );
                    // Tự động đóng modal sau 5 giây
                    setTimeout(function () {
                        $('#crawlModal').modal('hide');
                    }, 5000);
                } else {
                    $('#crawlResult').show().html(
                        '<div class="alert alert-warning">' +
                        '<h6>Không tìm thấy sản phẩm nào</h6>' +
                        '<p>Crawl hoàn tất nhưng không lấy được sản phẩm. ' +
                        'Vui lòng kiểm tra lại URL và thử lại.</p>' +
                        '</div>'
                    );
                }
            },
            error: function (xhr) {
                let errorMessage = 'Có lỗi xảy ra khi crawl dữ liệu';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                } else if (xhr.status === 502) {
                    errorMessage = 'Không thể kết nối đến trang GrabFood. Vui lòng kiểm tra lại URL.';
                }

                $('#crawlResult').show().html(`
                    <div class="alert alert-danger">
                        <h6>Crawl thất bại!</h6>
                        <p>${errorMessage}</p>
                    </div>
                `);
                $('#startCrawlBtn').prop('disabled', false).html('Bắt đầu Crawl');
            }
        });
    });

    // Reset form khi modal đóng
    $('#crawlModal').on('hidden.bs.modal', function () {
        $('#crawlForm')[0].reset();
        $('#crawlResult').hide().empty();
        $('#startCrawlBtn').prop('disabled', false).html('Bắt đầu Crawl');
    });
});