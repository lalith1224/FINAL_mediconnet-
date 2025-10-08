// Chatbot functionality for MediConnect
class MediConnectChatbot {
    constructor() {
        this.messages = [];
        this.isOpen = false;
        this.isLoading = false;
        this.init();
    }

    init() {
        this.createChatbotUI();
        this.attachEventListeners();
    }

    createChatbotUI() {
        const chatbotHTML = `
            <div id="chatbot-container" class="chatbot-container">
                <button id="chatbot-toggle" class="chatbot-toggle" title="AI Assistant">
                    <i class="fas fa-robot"></i>
                </button>
                <div id="chatbot-window" class="chatbot-window" style="display: none;">
                    <div class="chatbot-header">
                        <div class="chatbot-header-content">
                            <i class="fas fa-robot"></i>
                            <span>MediConnect AI Assistant</span>
                        </div>
                        <button id="chatbot-close" class="chatbot-close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div id="chatbot-messages" class="chatbot-messages">
                        <div class="chatbot-message bot-message">
                            <div class="message-avatar">
                                <i class="fas fa-robot"></i>
                            </div>
                            <div class="message-content">
                                <p>Hello! I'm your MediConnect AI assistant. How can I help you today?</p>
                            </div>
                        </div>
                    </div>
                    <div class="chatbot-input-container">
                        <input 
                            type="text" 
                            id="chatbot-input" 
                            class="chatbot-input" 
                            placeholder="Type your message..."
                            autocomplete="off"
                        />
                        <button id="chatbot-send" class="chatbot-send">
                            <i class="fas fa-paper-plane"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', chatbotHTML);
    }

    attachEventListeners() {
        const toggleBtn = document.getElementById('chatbot-toggle');
        const closeBtn = document.getElementById('chatbot-close');
        const sendBtn = document.getElementById('chatbot-send');
        const input = document.getElementById('chatbot-input');

        toggleBtn.addEventListener('click', () => this.toggleChat());
        closeBtn.addEventListener('click', () => this.closeChat());
        sendBtn.addEventListener('click', () => this.sendMessage());
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });
    }

    toggleChat() {
        const window = document.getElementById('chatbot-window');
        this.isOpen = !this.isOpen;
        window.style.display = this.isOpen ? 'flex' : 'none';
        
        if (this.isOpen) {
            document.getElementById('chatbot-input').focus();
        }
    }

    closeChat() {
        this.isOpen = false;
        document.getElementById('chatbot-window').style.display = 'none';
    }

    async sendMessage() {
        const input = document.getElementById('chatbot-input');
        const message = input.value.trim();

        if (!message || this.isLoading) return;

        // Add user message to UI
        this.addMessageToUI('user', message);
        input.value = '';

        // Add to messages array
        this.messages.push({ role: 'user', content: message });

        // Show loading indicator
        this.showLoading();

        try {
            const response = await fetch('/api/chatbot/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    messages: this.messages
                })
            });

            const data = await response.json();

            if (data.success) {
                // Add assistant message
                this.messages.push({ role: 'assistant', content: data.message });
                this.addMessageToUI('assistant', data.message);
            } else {
                this.addMessageToUI('error', data.error || 'An error occurred. Please try again.');
            }
        } catch (error) {
            console.error('Chatbot error:', error);
            this.addMessageToUI('error', 'Failed to connect to AI service. Please check your connection and try again.');
        } finally {
            this.hideLoading();
        }
    }

    addMessageToUI(role, content) {
        const messagesContainer = document.getElementById('chatbot-messages');
        const messageClass = role === 'user' ? 'user-message' : role === 'error' ? 'error-message' : 'bot-message';
        const icon = role === 'user' ? 'fa-user' : role === 'error' ? 'fa-exclamation-circle' : 'fa-robot';

        const messageHTML = `
            <div class="chatbot-message ${messageClass}">
                <div class="message-avatar">
                    <i class="fas ${icon}"></i>
                </div>
                <div class="message-content">
                    <p>${this.escapeHtml(content)}</p>
                </div>
            </div>
        `;

        messagesContainer.insertAdjacentHTML('beforeend', messageHTML);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    showLoading() {
        this.isLoading = true;
        const messagesContainer = document.getElementById('chatbot-messages');
        const loadingHTML = `
            <div class="chatbot-message bot-message" id="loading-message">
                <div class="message-avatar">
                    <i class="fas fa-robot"></i>
                </div>
                <div class="message-content">
                    <div class="typing-indicator">
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>
                </div>
            </div>
        `;
        messagesContainer.insertAdjacentHTML('beforeend', loadingHTML);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    hideLoading() {
        this.isLoading = false;
        const loadingMessage = document.getElementById('loading-message');
        if (loadingMessage) {
            loadingMessage.remove();
        }
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    clearChat() {
        this.messages = [];
        const messagesContainer = document.getElementById('chatbot-messages');
        messagesContainer.innerHTML = `
            <div class="chatbot-message bot-message">
                <div class="message-avatar">
                    <i class="fas fa-robot"></i>
                </div>
                <div class="message-content">
                    <p>Hello! I'm your MediConnect AI assistant. How can I help you today?</p>
                </div>
            </div>
        `;
    }
}

// Initialize chatbot when DOM is loaded
let chatbot;
document.addEventListener('DOMContentLoaded', () => {
    // Wait a bit to ensure user is logged in
    setTimeout(() => {
        chatbot = new MediConnectChatbot();
    }, 1000);
});
