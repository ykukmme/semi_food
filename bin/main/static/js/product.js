const NAMHAE_CART_STORAGE_KEY = "namhae_cart_v1";
const TOKEN_STORAGE_KEY = "accessToken";

let quantity = 1;
let minQty = 1;
let selectedSizeLabel = "기본";
let currentUnitPrice = 0;

function loadCartFromStorage() {
    try {
        const raw = localStorage.getItem(NAMHAE_CART_STORAGE_KEY);
        if (!raw) {
            return [];
        }

        const parsed = JSON.parse(raw);
        return Array.isArray(parsed) ? parsed : [];
    } catch {
        return [];
    }
}

function saveCartToStorage(items) {
    localStorage.setItem(NAMHAE_CART_STORAGE_KEY, JSON.stringify(items));
}

function updateCartBadge() {
    const badge = document.getElementById("cart-count-badge");
    if (!badge) {
        return;
    }

    const totalCount = loadCartFromStorage().reduce((sum, item) => {
        const itemQuantity = Number(item && item.quantity) || 0;
        return sum + itemQuantity;
    }, 0);

    badge.textContent = totalCount > 99 ? "99+" : String(totalCount);
    badge.classList.toggle("is-hidden", totalCount <= 0);
}

function formatWon(value) {
    const price = Number(value) || 0;
    return price > 0 ? `₩${price.toLocaleString("ko-KR")}` : "가격 미정";
}

function updatePrices() {
    const displayPrice = document.getElementById("display-price");
    const qtyValue = document.getElementById("qty-value");

    if (displayPrice) {
        displayPrice.textContent = formatWon(currentUnitPrice * quantity);
    }

    if (qtyValue) {
        qtyValue.textContent = String(quantity);
    }
}

function setupGallery() {
    const mainImage = document.getElementById("main-product-image");
    const thumbnails = document.querySelectorAll(".thumbnail-container");

    if (!mainImage || thumbnails.length === 0) {
        return;
    }

    thumbnails.forEach((thumbnail) => {
        thumbnail.addEventListener("click", () => {
            const thumbnailImage = thumbnail.querySelector("img");
            if (!thumbnailImage) {
                return;
            }

            mainImage.src = thumbnailImage.src;
            thumbnails.forEach((item) => item.classList.remove("active"));
            thumbnail.classList.add("active");
        });
    });
}

function setupOptions() {
    const displayPrice = document.getElementById("display-price");
    const sizeOptions = document.querySelectorAll(".size-option");

    currentUnitPrice = Number(displayPrice?.dataset.unitPrice) || 0;

    sizeOptions.forEach((option) => {
        option.addEventListener("click", () => {
            currentUnitPrice = Number(option.dataset.price) || currentUnitPrice;
            minQty = Number.parseInt(option.dataset.minQty, 10) || 1;
            quantity = minQty;
            selectedSizeLabel = option.dataset.size || option.textContent.trim();

            sizeOptions.forEach((item) => item.classList.remove("is-active"));
            option.classList.add("is-active");
            updatePrices();
        });
    });

    updatePrices();
}

function setupQuantityControls() {
    document.getElementById("qty-plus")?.addEventListener("click", () => {
        quantity += 1;
        updatePrices();
    });

    document.getElementById("qty-minus")?.addEventListener("click", () => {
        if (quantity <= minQty) {
            return;
        }

        quantity -= 1;
        updatePrices();
    });
}

function getCurrentProductLine() {
    const info = document.querySelector(".product-info-scroll");
    const titleEl = document.getElementById("product-title");
    const mainImage = document.getElementById("main-product-image");
    const productId = info?.dataset.productId || "product";
    const productTitle = titleEl ? titleEl.textContent.trim() : "상품";
    const imageSrc = mainImage ? mainImage.src : info?.dataset.productImage || "";

    return {
        id: `${productId}-${selectedSizeLabel}`,
        productId,
        name: `${productTitle} (${selectedSizeLabel})`,
        collection: "Heritage Namhae",
        price: currentUnitPrice,
        quantity,
        image: imageSrc,
        minQty
    };
}

function addCurrentProductToCart() {
    const line = getCurrentProductLine();
    const cart = loadCartFromStorage();
    const existingIndex = cart.findIndex((item) => item.id === line.id);

    if (existingIndex >= 0) {
        cart[existingIndex].quantity += line.quantity;
    } else {
        cart.push(line);
    }

    saveCartToStorage(cart);
}

async function saveCurrentProductToDatabase() {
    const line = getCurrentProductLine();
    const token = localStorage.getItem(TOKEN_STORAGE_KEY);

    if (!token) {
        window.location.href = "/login.html";
        return false;
    }

    const response = await fetch("/cart/items", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            productId: Number(line.productId),
            quantity: line.quantity
        })
    });

    if (response.status === 401 || response.status === 403) {
        window.location.href = "/login.html";
        return false;
    }

    if (!response.ok) {
        throw new Error("Failed to save cart item.");
    }

    return true;
}

function showNotification(message) {
    const notification = document.createElement("div");
    notification.className = "product-toast";
    notification.textContent = message;
    document.body.appendChild(notification);

    requestAnimationFrame(() => notification.classList.add("is-visible"));

    window.setTimeout(() => {
        notification.classList.remove("is-visible");
        window.setTimeout(() => notification.remove(), 300);
    }, 2000);
}

function setupCartButtons() {
    document.getElementById("btn-add-to-cart")?.addEventListener("click", async () => {
        addCurrentProductToCart();
        try {
            const saved = await saveCurrentProductToDatabase();
            if (!saved) {
                return;
            }
            updateCartBadge();
            showNotification("상품이 장바구니에 추가되었습니다.");
        } catch {
            showNotification("장바구니 저장에 실패했습니다.");
        }
    });

    document.getElementById("btn-go-to-cart")?.addEventListener("click", () => {
        window.location.href = "/cart.html";
    });

    document.getElementById("btn-buy-now")?.addEventListener("click", async () => {
        addCurrentProductToCart();
        try {
            const saved = await saveCurrentProductToDatabase();
            if (!saved) {
                return;
            }
            updateCartBadge();
            window.location.href = "/cart.html";
        } catch {
            showNotification("장바구니 저장에 실패했습니다.");
        }
    });
}

document.addEventListener("DOMContentLoaded", () => {
    setupGallery();
    setupOptions();
    setupQuantityControls();
    setupCartButtons();
    updateCartBadge();
});
