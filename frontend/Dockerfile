FROM node:18-alpine
WORKDIR /app

ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

COPY package.json ./
RUN npm install

COPY index.html vite.config.ts tsconfig.json ./
COPY src ./src

EXPOSE 5173
CMD ["npm", "run", "dev"]

