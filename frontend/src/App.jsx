import { useEffect, useMemo, useState } from 'react';
import { api } from './api.js';

const EMPTY_FORM = {
  name: '',
  keywords: [],
  bidAmount: '0.01',
  fund: '0.01',
  status: 'ON',
  town: '',
  radiusKm: '1',
};

function normaliseCampaign(campaign) {
  return {
    name: campaign.name ?? '',
    keywords: (campaign.keywords ?? []).map((keyword) => keyword.value),
    bidAmount: String(campaign.bidAmount ?? '0.01'),
    fund: String(campaign.fund ?? '0.01'),
    status: campaign.status ?? 'ON',
    town: campaign.town?.name ?? '',
    radiusKm: String(campaign.radiusKm ?? '1'),
  };
}

function money(value) {
  if (value === null || value === undefined) return '—';
  return new Intl.NumberFormat('pl-PL', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(Number(value));
}

export default function App() {
  const [sellers, setSellers] = useState([]);
  const [sellerName, setSellerName] = useState('');
  const [seller, setSeller] = useState(null);
  const [productName, setProductName] = useState('');
  const [campaignNames, setCampaignNames] = useState([]);
  const [selectedCampaign, setSelectedCampaign] = useState('');
  const [originalCampaignName, setOriginalCampaignName] = useState('');
  const [towns, setTowns] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [keywordInput, setKeywordInput] = useState('');
  const [keywordSuggestions, setKeywordSuggestions] = useState([]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const products = seller?.products ?? [];
  const isEditing = Boolean(originalCampaignName);

  const availableSuggestions = useMemo(
    () => keywordSuggestions.filter(({ value }) => !form.keywords.includes(value)),
    [keywordSuggestions, form.keywords],
  );

  function clearMessages() {
    setError('');
    setNotice('');
  }

  function resetForm(defaultTown = towns[0]?.name ?? '') {
    setForm({ ...EMPTY_FORM, town: defaultTown });
    setSelectedCampaign('');
    setOriginalCampaignName('');
    setKeywordInput('');
    setKeywordSuggestions([]);
  }

  async function refreshSeller(name = sellerName) {
    if (!name) return null;
    const data = await api.getSeller(name);
    setSeller(data);
    return data;
  }

  async function refreshCampaignNames(
    currentSeller = sellerName,
    currentProduct = productName,
  ) {
    if (!currentSeller || !currentProduct) {
      setCampaignNames([]);
      return [];
    }

    const names = await api.getCampaignNames(currentSeller, currentProduct);
    setCampaignNames(names);
    return names;
  }

  async function loadCampaign(name) {
    if (!sellerName || !productName || !name) return;

    setBusy(true);
    clearMessages();
    try {
      const campaign = await api.getCampaign(sellerName, productName, name);
      setForm(normaliseCampaign(campaign));
      setSelectedCampaign(name);
      setOriginalCampaignName(name);
      setKeywordInput('');
      setKeywordSuggestions([]);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  useEffect(() => {
    let cancelled = false;

    async function loadReferenceData() {
      setBusy(true);
      try {
        const [sellerList, townList] = await Promise.all([
          api.getSellers(),
          api.getTowns(),
        ]);
        if (cancelled) return;

        setSellers(sellerList);
        setTowns(townList);
        setForm((current) => ({
          ...current,
          town: current.town || townList[0]?.name || '',
        }));
        setSellerName(sellerList[0]?.name ?? '');
      } catch (requestError) {
        if (!cancelled) setError(requestError.message);
      } finally {
        if (!cancelled) setBusy(false);
      }
    }

    loadReferenceData();
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!sellerName) {
      setSeller(null);
      setProductName('');
      return;
    }

    let cancelled = false;

    async function loadSelectedSeller() {
      setBusy(true);
      clearMessages();
      try {
        // Nie korzystamy z products zwróconych przez GET /sellers, ponieważ
        // obecny backend mapuje je błędnie. Szczegóły sprzedawcy są poprawne.
        const data = await api.getSeller(sellerName);
        if (cancelled) return;

        setSeller(data);
        setProductName(data.products?.[0]?.name ?? '');
        setCampaignNames([]);
        resetForm();
      } catch (requestError) {
        if (!cancelled) setError(requestError.message);
      } finally {
        if (!cancelled) setBusy(false);
      }
    }

    loadSelectedSeller();
    return () => {
      cancelled = true;
    };
    // towns intentionally omitted: changing reference data must not reload seller.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sellerName]);

  useEffect(() => {
    if (!sellerName || !productName) {
      setCampaignNames([]);
      resetForm();
      return;
    }

    let cancelled = false;

    async function loadNames() {
      setBusy(true);
      clearMessages();
      try {
        const names = await api.getCampaignNames(sellerName, productName);
        if (cancelled) return;
        setCampaignNames(names);
        resetForm();
      } catch (requestError) {
        if (!cancelled) setError(requestError.message);
      } finally {
        if (!cancelled) setBusy(false);
      }
    }

    loadNames();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sellerName, productName]);

  useEffect(() => {
    const prefix = keywordInput.trim();
    if (!prefix) {
      setKeywordSuggestions([]);
      return undefined;
    }

    const timer = window.setTimeout(async () => {
      try {
        const suggestions = await api.getKeywordSuggestions(prefix);
        setKeywordSuggestions(suggestions);
      } catch {
        setKeywordSuggestions([]);
      }
    }, 200);

    return () => window.clearTimeout(timer);
  }, [keywordInput]);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function addKeyword(value) {
    if (!value || form.keywords.includes(value)) return;
    setForm((current) => ({
      ...current,
      keywords: [...current.keywords, value],
    }));
    setKeywordInput('');
    setKeywordSuggestions([]);
  }

  function removeKeyword(value) {
    setForm((current) => ({
      ...current,
      keywords: current.keywords.filter((keyword) => keyword !== value),
    }));
  }

  function payload() {
    return {
      name: form.name.trim(),
      keywords: form.keywords,
      bidAmount: Number(form.bidAmount),
      fund: Number(form.fund),
      status: form.status,
      town: form.town,
      radiusKm: Number(form.radiusKm),
    };
  }

  async function submit(event) {
    event.preventDefault();
    clearMessages();

    if (!sellerName || !productName) {
      setError('Wybierz sprzedawcę i produkt.');
      return;
    }
    if (form.keywords.length === 0) {
      setError('Dodaj co najmniej jedno słowo kluczowe.');
      return;
    }

    setBusy(true);
    try {
      const data = payload();
      if (isEditing) {
        await api.updateCampaign(
          sellerName,
          productName,
          originalCampaignName,
          data,
        );
        await Promise.all([
          refreshSeller(),
          refreshCampaignNames(),
        ]);
        const updated = await api.getCampaign(
          sellerName,
          productName,
          data.name,
        );
        setForm(normaliseCampaign(updated));
        setSelectedCampaign(data.name);
        setOriginalCampaignName(data.name);
        setNotice('Zapisano.');
      } else {
        const created = await api.createCampaign(sellerName, productName, data);
        await Promise.all([
          refreshSeller(),
          refreshCampaignNames(),
        ]);
        setForm(normaliseCampaign(created));
        setSelectedCampaign(created.name);
        setOriginalCampaignName(created.name);
        setNotice('Utworzono.');
      }
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  async function removeCampaign() {
    if (!isEditing) return;
    clearMessages();

    const accepted = window.confirm(`Usunąć kampanię „${originalCampaignName}”?`);
    if (!accepted) return;

    setBusy(true);
    try {
      await api.deleteCampaign(
        sellerName,
        productName,
        originalCampaignName,
      );
      await Promise.all([
        refreshSeller(),
        refreshCampaignNames(),
      ]);
      resetForm();
      setNotice('Usunięto.');
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="page">
      <section className="panel">
        <header className="topbar">
          <h1>Simple Campaigns</h1>
          <div className="balance">
            Saldo: <strong>{money(seller?.accountBalance)}</strong>
          </div>
        </header>

        <div className="selectors">
          <label>
            Sprzedawca
            <select
              value={sellerName}
              onChange={(event) => setSellerName(event.target.value)}
              disabled={busy || sellers.length === 0}
            >
              {sellers.map((item) => (
                <option key={item.name} value={item.name}>
                  {item.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            Produkt
            <select
              value={productName}
              onChange={(event) => setProductName(event.target.value)}
              disabled={busy || products.length === 0}
            >
              {products.length === 0 && <option value="">Brak produktów</option>}
              {products.map((product) => (
                <option key={product.name} value={product.name}>
                  {product.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            Kampania
            <select
              value={selectedCampaign}
              onChange={(event) => {
                const name = event.target.value;
                if (name) loadCampaign(name);
                else resetForm();
              }}
              disabled={busy || !productName}
            >
              <option value="">Nowa kampania</option>
              {campaignNames.map((name) => (
                <option key={name} value={name}>
                  {name}
                </option>
              ))}
            </select>
          </label>
        </div>

        <form onSubmit={submit}>
          <div className="grid">
            <label className="wide">
              Nazwa kampanii
              <input
                name="name"
                value={form.name}
                onChange={updateField}
                maxLength={255}
                required
              />
            </label>

            <div className="field wide">
              <span>Słowa kluczowe</span>
              <div className="chips">
                {form.keywords.map((keyword) => (
                  <button
                    type="button"
                    className="chip"
                    key={keyword}
                    onClick={() => removeKeyword(keyword)}
                    aria-label={`Usuń ${keyword}`}
                  >
                    {keyword} ×
                  </button>
                ))}
              </div>
              <div className="typeahead">
                <input
                  value={keywordInput}
                  onChange={(event) => setKeywordInput(event.target.value)}
                  onKeyDown={(event) => {
                    if (event.key === 'Enter' && availableSuggestions[0]) {
                      event.preventDefault();
                      addKeyword(availableSuggestions[0].value);
                    }
                  }}
                  placeholder="Wpisz początek słowa"
                  maxLength={255}
                  autoComplete="off"
                />
                {availableSuggestions.length > 0 && (
                  <div className="suggestions">
                    {availableSuggestions.map((suggestion) => (
                      <button
                        type="button"
                        key={suggestion.value}
                        onClick={() => addKeyword(suggestion.value)}
                      >
                        {suggestion.value}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>

            <label>
              Stawka
              <input
                name="bidAmount"
                type="number"
                min="0.01"
                step="0.01"
                value={form.bidAmount}
                onChange={updateField}
                required
              />
            </label>

            <label>
              Fundusz
              <input
                name="fund"
                type="number"
                min="0.01"
                step="0.01"
                value={form.fund}
                onChange={updateField}
                required
              />
            </label>

            <label>
              Status
              <select name="status" value={form.status} onChange={updateField}>
                <option value="ON">ON</option>
                <option value="OFF">OFF</option>
              </select>
            </label>

            <label>
              Miasto
              <select name="town" value={form.town} onChange={updateField} required>
                <option value="" disabled>Wybierz miasto</option>
                {towns.map((town) => (
                  <option key={town.name} value={town.name}>
                    {town.name}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Promień (km)
              <input
                name="radiusKm"
                type="number"
                min="1"
                step="1"
                value={form.radiusKm}
                onChange={updateField}
                required
              />
            </label>
          </div>

          {error && <p className="message error">{error}</p>}
          {notice && <p className="message success">{notice}</p>}

          <div className="actions">
            <button type="submit" disabled={busy || !sellerName || !productName}>
              {isEditing ? 'Zapisz' : 'Utwórz'}
            </button>
            {isEditing && (
              <button
                type="button"
                className="danger"
                onClick={removeCampaign}
                disabled={busy}
              >
                Usuń
              </button>
            )}
            {isEditing && (
              <button
                type="button"
                className="secondary"
                onClick={() => resetForm()}
                disabled={busy}
              >
                Nowa
              </button>
            )}
          </div>
        </form>
      </section>
    </main>
  );
}
