# MediConnect AI Chatbot Integration Guide

## Overview
This guide explains how to set up and use the AI chatbot feature integrated into the MediConnect Healthcare Management System. The chatbot uses OpenRouter API to provide intelligent assistance to both doctors and patients.

## Features
- 🤖 **AI-Powered Assistant**: Uses OpenRouter API with Llama 3.1 model (free tier)
- 👨‍⚕️ **Role-Based Context**: Different system prompts for doctors and patients
- 💬 **Real-time Chat**: Interactive chat interface with typing indicators
- 🔒 **Secure**: Requires authentication to access chatbot
- 📱 **Responsive Design**: Works on desktop and mobile devices
- 🎨 **Modern UI**: Beautiful gradient design with smooth animations

## Setup Instructions

### Step 1: Get Your OpenRouter API Key

1. Visit [OpenRouter](https://openrouter.ai/)
2. Sign up for a free account
3. Navigate to [API Keys](https://openrouter.ai/keys)
4. Create a new API key
5. Copy the API key (it will look like: `sk-or-v1-...`)

### Step 2: Configure the API Key

Open the file:
```
src/main/resources/application.properties
```

Find the line:
```properties
openrouter.api.key=YOUR_OPENROUTER_API_KEY_HERE
```

Replace `YOUR_OPENROUTER_API_KEY_HERE` with your actual API key:
```properties
openrouter.api.key=sk-or-v1-your-actual-key-here
```

### Step 3: (Optional) Customize the AI Model

By default, the chatbot uses the free Llama 3.1 8B model. You can change this in `application.properties`:

```properties
# Available free models:
openrouter.model=meta-llama/llama-3.1-8b-instruct:free
# openrouter.model=google/gemma-2-9b-it:free
# openrouter.model=mistralai/mistral-7b-instruct:free

# Premium models (requires credits):
# openrouter.model=openai/gpt-4-turbo
# openrouter.model=anthropic/claude-3-opus
```

### Step 4: Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## How to Use the Chatbot

### For Patients:
1. Log in to your patient account
2. Navigate to the Patient Dashboard
3. Look for the purple robot icon in the bottom-right corner
4. Click the icon to open the chatbot
5. Ask questions like:
   - "What should I do if I miss a dose of my medication?"
   - "How can I prepare for my upcoming appointment?"
   - "What are the symptoms of the flu?"
   - "Can you explain what a blood pressure reading means?"

### For Doctors:
1. Log in to your doctor account
2. Navigate to the Doctor Dashboard
3. Look for the purple robot icon in the bottom-right corner
4. Click the icon to open the chatbot
5. Ask questions like:
   - "What are the latest guidelines for hypertension treatment?"
   - "How should I manage a patient with diabetes and high cholesterol?"
   - "What are the contraindications for aspirin?"
   - "Can you help me understand this medical abbreviation?"

## Technical Architecture

### Backend Components

#### 1. ChatbotController (`/api/chatbot`)
- **POST /api/chatbot/chat**: Send messages to the AI
- **GET /api/chatbot/status**: Check chatbot availability

#### 2. ChatbotService
- Handles communication with OpenRouter API
- Manages role-based system prompts
- Processes chat requests and responses

#### 3. DTOs
- `ChatMessage`: Individual message structure
- `ChatRequest`: Request payload with message history
- `ChatResponse`: Response with AI-generated message

### Frontend Components

#### 1. chatbot.js
- `MediConnectChatbot` class: Main chatbot logic
- Message handling and UI updates
- API communication

#### 2. chatbot.css
- Modern, responsive styling
- Gradient design with animations
- Mobile-friendly layout

## API Endpoints

### POST /api/chatbot/chat
Send a message to the chatbot.

**Request:**
```json
{
  "messages": [
    {
      "role": "user",
      "content": "What are the symptoms of diabetes?"
    }
  ]
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Common symptoms of diabetes include...",
  "role": "assistant"
}
```

**Response (Error):**
```json
{
  "success": false,
  "error": "API Error: Invalid API key"
}
```

### GET /api/chatbot/status
Check if the chatbot is available.

**Response:**
```json
{
  "status": "online",
  "userRole": "PATIENT",
  "message": "Chatbot is ready to assist you"
}
```

## Customization

### Changing System Prompts

Edit the `getSystemPrompt()` method in `ChatbotService.java`:

```java
private String getSystemPrompt(String userRole) {
    if ("DOCTOR".equalsIgnoreCase(userRole)) {
        return "Your custom doctor prompt here...";
    } else if ("PATIENT".equalsIgnoreCase(userRole)) {
        return "Your custom patient prompt here...";
    }
    // ...
}
```

### Adjusting AI Parameters

In `ChatbotService.java`, modify the request payload:

```java
payload.put("temperature", 0.7);  // 0.0 = deterministic, 1.0 = creative
payload.put("max_tokens", 1000);  // Maximum response length
```

### Styling the Chatbot

Edit `src/main/resources/static/css/chatbot.css` to customize:
- Colors and gradients
- Button sizes and positions
- Animation speeds
- Font sizes

## Troubleshooting

### Issue: "OpenRouter API key is not configured"
**Solution**: Make sure you've added your API key to `application.properties`

### Issue: "Failed to communicate with AI service"
**Solution**: 
- Check your internet connection
- Verify your API key is valid
- Check OpenRouter service status

### Issue: Chatbot button not appearing
**Solution**:
- Make sure you're logged in
- Check browser console for JavaScript errors
- Verify `chatbot.js` and `chatbot.css` are loaded

### Issue: "API Error: Insufficient credits"
**Solution**:
- Switch to a free model in `application.properties`
- Add credits to your OpenRouter account

## Security Considerations

1. **API Key Protection**: Never commit your API key to version control
2. **Use Environment Variables**: For production, use environment variables:
   ```bash
   export OPENROUTER_API_KEY=your-key-here
   ```
   Then in `application.properties`:
   ```properties
   openrouter.api.key=${OPENROUTER_API_KEY}
   ```

3. **Rate Limiting**: Consider implementing rate limiting to prevent abuse
4. **Input Validation**: The system validates user input before sending to API

## Cost Management

### Free Tier
- Llama 3.1 8B Instruct: Free
- Gemma 2 9B: Free
- Mistral 7B: Free

### Paid Models
- Check [OpenRouter Pricing](https://openrouter.ai/docs#models) for costs
- Monitor usage in your OpenRouter dashboard
- Set spending limits in your account settings

## Support

For issues or questions:
1. Check the [OpenRouter Documentation](https://openrouter.ai/docs)
2. Review the Spring Boot logs for error messages
3. Check browser console for frontend errors

## Future Enhancements

Potential improvements:
- [ ] Add conversation history persistence
- [ ] Implement voice input/output
- [ ] Add file upload for medical documents
- [ ] Create specialized medical knowledge base
- [ ] Add multi-language support
- [ ] Implement conversation export feature

## License

This chatbot integration is part of the MediConnect Healthcare Management System.
