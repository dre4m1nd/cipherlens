# ---------- 构建阶段 ----------
FROM node:18-alpine AS build
WORKDIR /app

ARG NPM_REGISTRY=https://registry.npmjs.org
ENV NPM_REGISTRY=$NPM_REGISTRY

COPY package.json package-lock.json ./
RUN npm ci --registry=$NPM_REGISTRY --no-audit --no-fund

COPY . .
RUN npm run build

# ---------- 运行阶段 ----------
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
