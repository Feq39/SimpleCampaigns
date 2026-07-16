const JSON_HEADERS = {
  'Content-Type': 'application/json',
};

function segment(value) {
  return encodeURIComponent(value);
}

function extractErrorMessage(data, status) {
  if (!data) return `Błąd HTTP ${status}`;
  if (typeof data === 'string') return data;

  if (data.message) return data.message;
  if (data.detail) return data.detail;
  if (data.error) return data.error;

  if (data.errors && typeof data.errors === 'object') {
    const messages = Object.values(data.errors).flat().filter(Boolean);
    if (messages.length > 0) return messages.join(', ');
  }

  return `Błąd HTTP ${status}`;
}

async function request(path, options = {}) {
  const response = await fetch(path, options);
  const contentType = response.headers.get('content-type') ?? '';
  const hasJson = contentType.includes('application/json');
  const body = response.status === 204
    ? null
    : hasJson
      ? await response.json()
      : await response.text();

  if (!response.ok) {
    throw new Error(extractErrorMessage(body, response.status));
  }

  return body;
}

export const api = {
  getSellers() {
    return request('/api/v1/sellers');
  },

  getSeller(name) {
    return request(`/api/v1/sellers/${segment(name)}`);
  },

  getTowns() {
    return request('/api/towns/v1');
  },

  getKeywordSuggestions(prefix) {
    const query = new URLSearchParams({ prefix });
    return request(`/api/keywords/v1/typeahead?${query}`);
  },

  getCampaignNames(sellerName, productName) {
    return request(
      `/api/v1/campaigns/${segment(sellerName)}/${segment(productName)}`,
    );
  },

  getCampaign(sellerName, productName, campaignName) {
    return request(
      `/api/v1/campaigns/${segment(sellerName)}/${segment(productName)}/${segment(campaignName)}`,
    );
  },

  createCampaign(sellerName, productName, payload) {
    return request(
      `/api/v1/campaigns/${segment(sellerName)}/${segment(productName)}`,
      {
        method: 'POST',
        headers: JSON_HEADERS,
        body: JSON.stringify(payload),
      },
    );
  },

  updateCampaign(sellerName, productName, campaignName, payload) {
    return request(
      `/api/v1/campaigns/${segment(sellerName)}/${segment(productName)}/${segment(campaignName)}`,
      {
        method: 'PUT',
        headers: JSON_HEADERS,
        body: JSON.stringify(payload),
      },
    );
  },

  deleteCampaign(sellerName, productName, campaignName) {
    return request(
      `/api/v1/campaigns/${segment(sellerName)}/${segment(productName)}/${segment(campaignName)}`,
      { method: 'DELETE' },
    );
  },
};
