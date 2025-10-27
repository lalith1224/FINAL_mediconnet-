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

            if (response.ok) {
                const data = await response.json();

                if (data.success && data.message) {
                    // Add assistant message
                    this.messages.push({ role: 'assistant', content: data.message });
                    this.addMessageToUI('assistant', data.message);
                } else {
                    this.addMessageToUI('error', data.error || 'I apologize, but I couldn\'t generate a proper response. Please try rephrasing your question.');
                }
            } else {
                // Handle HTTP errors
                let errorMessage = 'I\'m having trouble connecting to the AI service.';
                if (response.status === 401) {
                    errorMessage = 'Please log in to use the AI assistant.';
                } else if (response.status === 403) {
                    errorMessage = 'You don\'t have permission to use the AI assistant.';
                } else if (response.status >= 500) {
                    errorMessage = 'The AI service is temporarily unavailable. Please try again later.';
                }
                this.addMessageToUI('error', errorMessage);
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

        // Format content for better display
        const formattedContent = this.formatMessage(content, role);

        const messageHTML = `
            <div class="chatbot-message ${messageClass}">
                <div class="message-avatar">
                    <i class="fas ${icon}"></i>
                </div>
                <div class="message-content">
                    ${formattedContent}
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

    formatMessage(content, role) {
        if (role === 'user') {
            return `<p>${this.escapeHtml(content)}</p>`;
        }

        // For bot messages, apply basic formatting
        let formatted = this.escapeHtml(content);
        
        // Convert line breaks to paragraphs
        formatted = formatted.split('\n\n').map(paragraph => {
            if (paragraph.trim()) {
                return `<p>${paragraph.trim()}</p>`;
            }
            return '';
        }).join('');

        // Handle bullet points
        formatted = formatted.replace(/^- (.+)$/gm, '<li>$1</li>');
        formatted = formatted.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');

        // Handle numbered lists
        formatted = formatted.replace(/^\d+\. (.+)$/gm, '<li>$1</li>');
        formatted = formatted.replace(/(<li>.*<\/li>)/s, '<ol>$1</ol>');

        // Handle bold text (basic markdown)
        formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        formatted = formatted.replace(/\*(.*?)\*/g, '<em>$1</em>');

        // If no paragraphs were created, wrap in a single paragraph
        if (!formatted.includes('<p>') && !formatted.includes('<ul>') && !formatted.includes('<ol>')) {
            formatted = `<p>${formatted}</p>`;
        }

        return formatted;
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
    // Wait a bit to ensure user is logged in and check if we're on a dashboard page
    setTimeout(() => {
        // Only initialize chatbot if user is on a dashboard page (logged in)
        const isDashboard = document.querySelector('.dashboard') && 
                           document.querySelector('.dashboard').style.display !== 'none';
        const isLandingPage = document.getElementById('landing-page') && 
                             document.getElementById('landing-page').style.display !== 'none';
        
        // Don't show chatbot on landing, login, or register pages
        if (!isLandingPage && (isDashboard || window.location.pathname !== '/')) {
            chatbot = new MediConnectChatbot();
        }
    }, 1500);
});

// Function to initialize chatbot when user logs in
function initializeChatbot() {
    if (!chatbot) {
        chatbot = new MediConnectChatbot();
    }
}

// Function to destroy chatbot when user logs out
function destroyChatbot() {
    if (chatbot) {
        const container = document.getElementById('chatbot-container');
        if (container) {
            container.remove();
        }
        chatbot = null;
    }
}
