# Quick Start Guide - AI Chatbot

## 🚀 Get Started in 3 Steps

### 1️⃣ Get Your API Key
Visit: https://openrouter.ai/keys
- Sign up (free)
- Create API key
- Copy the key (starts with `sk-or-v1-`)

### 2️⃣ Add API Key to Configuration
Edit: `src/main/resources/application.properties`

```properties
openrouter.api.key=sk-or-v1-YOUR-KEY-HERE
```

### 3️⃣ Run the Application
```bash
mvn spring-boot:run
```

## ✅ That's It!

Open http://localhost:8080, login, and look for the purple robot icon 🤖 in the bottom-right corner!

## 📝 Files Created

### Backend:
- `src/main/java/com/mediconnect/controller/ChatbotController.java`
- `src/main/java/com/mediconnect/service/ChatbotService.java`
- `src/main/java/com/mediconnect/dto/ChatMessage.java`
- `src/main/java/com/mediconnect/dto/ChatRequest.java`
- `src/main/java/com/mediconnect/dto/ChatResponse.java`

### Frontend:
- `src/main/resources/static/js/chatbot.js`
- `src/main/resources/static/css/chatbot.css`

### Configuration:
- Updated: `src/main/resources/application.properties`
- Updated: `src/main/java/com/mediconnect/config/SecurityConfig.java`
- Updated: `src/main/resources/static/index.html`

## 🎯 Features

✨ **Role-Based AI Assistant**
- Different prompts for doctors and patients
- Context-aware responses

💬 **Modern Chat Interface**
- Beautiful gradient design
- Typing indicators
- Smooth animations
- Mobile responsive

🔒 **Secure & Private**
- Requires authentication
- Session-based access
- Secure API communication

## 💡 Example Questions

### For Patients:
- "What should I do if I have a fever?"
- "How can I manage my diabetes?"
- "What are the side effects of aspirin?"

### For Doctors:
- "What are the latest hypertension guidelines?"
- "How should I treat a patient with both diabetes and hypertension?"
- "What are the contraindications for metformin?"

## 🆘 Need Help?

See the full documentation: `CHATBOT_SETUP.md`

## 🎨 Customization

Want to change the colors or position? Edit:
- `src/main/resources/static/css/chatbot.css`

Want to change AI behavior? Edit:
- `src/main/java/com/mediconnect/service/ChatbotService.java`

## 📊 API Endpoints

- `POST /api/chatbot/chat` - Send messages
- `GET /api/chatbot/status` - Check status

## 🔧 Troubleshooting

**Chatbot not appearing?**
- Make sure you're logged in
- Check browser console for errors

**API errors?**
- Verify your API key is correct
- Check internet connection
- Ensure you're using a free model

**Need a different AI model?**
Edit `application.properties`:
```properties
openrouter.model=meta-llama/llama-3.1-8b-instruct:free
```

---

**Enjoy your new AI-powered chatbot! 🎉**
